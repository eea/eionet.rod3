package eionet.rod;

import eionet.rod.model.ObligationCountry;
import eionet.rod.model.Obligations;
import eionet.rod.service.ObligationService;
import eionet.rod.service.SpatialService;
import eionet.rod.util.RODUtil;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Lazy(false)
@Service("deadlineDaemon")
public class DeadlinesDaemon {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeadlinesDaemon.class);

    @Autowired
    ObligationService obligationService;

    @Autowired
    SpatialService spatialService;

    /**
     * The file to use in order to detect that the job ran today (not to flood the users with e-mails)
     */
    @Value("${storage.dir}/deadlines-daemon-datefile.txt")
    String fileName;

    @Value("${percent.of.freq:10}")
    Integer percentOfFreq;

    @Scheduled(cron = "${deadlinedaemon.job.cron}")
    @SchedulerLock(name = "deadlineDaemonLock")
    @Transactional
    public void execute() {
        LockAssert.assertLocked();
        FileWriter out = null;
        LOGGER.info( "DeadlinesDaemon scheduled task is running...");
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy");
            String d = formatter.format(new Date());
            Date today = formatter.parse(d);

            File file = new File(fileName);
            file.createNewFile();
            FileReader in = new FileReader(file);
            BufferedReader br = new BufferedReader(in);
            String result = br.readLine();
            in.close();
            if (result != null) {
                Date date = formatter.parse(result);
                if (date.before(today)) {
                    out = new FileWriter(file);
                    makeStructure(date);
                    out.write(d);
                }
            } else {
                out = new FileWriter(file);
                makeStructure(today);
                out.write(d);
            }
            LOGGER.info("DeadlinesDaemon scheduled task has finished.");
        } catch (Exception e) {
            LOGGER.error("Error in DeadlinesDaemon " + e.getMessage(), e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOGGER.debug(e.getMessage(), e);
                }
            }
        }
    }

    private void makeStructure(Date date) {

        Vector<Vector<String>> lists = new Vector<>();
        int days = (int) ((percentOfFreq / 100.0) * 30.0);

        List<Obligations> deadlines = obligationService.getUpcomingDeadlines(days);
        long timestamp = System.currentTimeMillis();

        for (Obligations o : deadlines) {
            LOGGER.info( "makeStructure {}", o.getOblTitle());
            long nextDeadlineMillis = o.getNextDeadline().getTime();

            String freq = o.getReportFreqMonths();
            int f = Integer.parseInt(freq);
            int period = days * f;
            long periodMillis = ((long) period * 24L * 3600L * 1000L);
            Date periodStartDate = new Date(nextDeadlineMillis - periodMillis);

            if (!periodStartDate.before(date)) {

                Vector<String> list = new Vector<>();
                String events = "http://rod.eionet.europa.eu/events/" + timestamp;

                list.add(events);
                list.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
                list.add(Attrs.SCHEMA_RDF + "Deadlineevent");
                lists.add(list);

                list = new Vector<>();
                list.add(events);
                list.add(Attrs.SCHEMA_RDF + "event_type");
                list.add("Deadlineevent");
                lists.add(list);

                list = new Vector<>();
                list.add(events);
                list.add("http://purl.org/dc/elements/1.1/title"); // Must match UNS placeholder $EVENT.TITLE
                list.add("Approaching deadline on " + o.getOblTitle());
                lists.add(list);

                list = new Vector<>();
                list.add(events);
                list.add(Attrs.SCHEMA_RDF + "nextdeadline");
                list.add(RODUtil.formatDate(o.getNextDeadline()));
                lists.add(list);

                list = new Vector<>();
                list.add(events);
                list.add(Attrs.SCHEMA_RDF + "nextdeadline2");
                list.add(RODUtil.formatDate(o.getNextDeadline2()));
                lists.add(list);

                list = new Vector<>();
                list.add(events);
                list.add(Attrs.SCHEMA_RDF + "obligation");
                list.add(o.getOblTitle());
                lists.add(list);

                List<ObligationCountry> countries = spatialService.findObligationCountriesList(o.getObligationId());
                for (ObligationCountry oc : countries) {
                    String country = oc.getCountryName();
                    if (country != null && !country.equals("")) {
                        list = new Vector<>();
                        list.add(events);
                        list.add(Attrs.SCHEMA_RDF + "locality");
                        list.add(country);
                        lists.add(list);
                    }
                }

                list = new Vector<>();
                list.add(events);
                list.add(Attrs.SCHEMA_RDF + "responsiblerole");
                list.add(o.getResponsibleRole());
                lists.add(list);

                list = new Vector<>();
                list.add(events);
                list.add("http://purl.org/dc/elements/1.1/identifier");
                String url = "http://rod.eionet.europa.eu/obligations/" + o.getObligationId();
                list.add(url);
                lists.add(list);

                if (!lists.isEmpty()) {
                    UNSEventSender.makeCall(lists);
                }
            }
        }
    }

}
