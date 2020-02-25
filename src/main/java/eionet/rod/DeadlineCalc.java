/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is "EINRC-5 / WebROD Project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2002 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Ander Tenno (TietoEnator)
 */

package eionet.rod;

import eionet.rod.model.Obligations;
import eionet.rod.service.ObligationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Lazy(false)
@Service("deadlineCalc")
public class DeadlineCalc {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeadlineCalc.class);

    @Autowired
    ObligationService obligationService;

    @Scheduled(cron = "${deadlinecalc.job.cron}")
    @Transactional
    public void execute() {
        List<Obligations> deadlines;

        LOGGER.info("DeadlineCalc v1.0 - getting deadlines...");

        // Get deadlines
        //
        try {
            deadlines = obligationService.getDeadlines();
        } catch (Exception e) {
            LOGGER.error("Getting deadlines from database failed. The following error was reported:\n" + e);
            LOGGER.error(e.getMessage(), e);
            return;
        }
        if (deadlines == null || deadlines.size() == 0) {
            LOGGER.info("0 deadlines found");
            return;
        }
        LOGGER.info(deadlines.size() + " deadlines found, updating...");

        // Update deadlines and save them back to the database
        for(Obligations o : deadlines){
            calculate(o);
        }

        // SELECT PK_RA_ID, FIRST_REPORTING, REPORT_FREQ_MONTHS, VALID_TO, TERMINATE "
        //           0           1               2                3           4

        LOGGER.info("Update complete.");
    }

    private void calculate(Obligations o){
        GregorianCalendar currDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));

        LOGGER.debug("Calculation for obligation " + o.getObligationId());
        Date current = o.getNextDeadline();

        // set termination to true if past the valid time
        if (!(currDate.after(o.getValidTo()) ? "Y" : "N").equals(o.getTerminate())) {
            obligationService.updateTermination(o.getObligationId(), "Y");
        }

        if (o.getReportFreqMonths() == null || o.getReportFreqMonths().equals("0")) {
            return;
        }

        Integer freq = Integer.parseInt(o.getReportFreqMonths());


        Calendar repDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        repDate.setTime(o.getFirstReporting());

        int day = repDate.get(Calendar.DATE);

        int m;

        Calendar validTo = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        validTo.setTime(o.getValidTo());
        repDate = calculateNextDeadline(currDate, repDate, validTo, freq);
        if(repDate == null) {
            LOGGER.error("Calculation stopped for obligation " + o.getObligationId() + " parameters " + formatCalendar(currDate) + " / " + formatCalendar(repDate) + " / " + formatCalendar(validTo) + " f " + freq);
            return;
        }

        if (repDate.before(currDate))
            obligationService.updateTermination(o.getObligationId(), "Y");

        Date nextDeadline = repDate.getTime();

        // Deadline after the next
        //
        if (day < 28)
            repDate.add(Calendar.MONTH, freq);
        else {
            repDate.add(Calendar.DATE, -3);
            repDate.add(Calendar.MONTH, freq);
            m = repDate.get(Calendar.MONTH);
            while (repDate.get(Calendar.MONTH) == m)
                repDate.add(Calendar.DATE, 1);
            repDate.add(Calendar.DATE, -1);
        }

        Date nextDeadline2 = null;

        if (!repDate.after(o.getValidTo()))
            nextDeadline2 = repDate.getTime();

        obligationService.updateDeadlines(o.getObligationId(), nextDeadline, nextDeadline2, current);
    }

    Calendar calculateNextDeadline(Calendar currDate, Calendar firstReporting, Calendar validTo, Integer freq) {
        LOGGER.debug("new:  " + formatCalendar(currDate) + " " + formatCalendar(firstReporting) + " " + formatCalendar(validTo));
        if(freq <= 0) {
            return null;
        }

        int m;
        int day = firstReporting.get(Calendar.DATE);
        currDate.add(Calendar.DATE, -3 * freq);
        if (day < 28) {
            while (firstReporting.before(currDate) && firstReporting.before(validTo) && firstReporting.get(Calendar.YEAR) < 2100)
                firstReporting.add(Calendar.MONTH, freq);
            if (firstReporting.after(validTo))
                firstReporting.add(Calendar.MONTH, -freq);
        } else {
            firstReporting.add(Calendar.DATE, -3);
            while (firstReporting.before(currDate) && firstReporting.before(validTo) && firstReporting.get(Calendar.YEAR) < 2100)
                firstReporting.add(Calendar.MONTH, freq);
            if (firstReporting.after(validTo))
                firstReporting.add(Calendar.MONTH, -freq);
            GregorianCalendar rewindDate = (GregorianCalendar) firstReporting.clone(); // Save for check below
            m = firstReporting.get(Calendar.MONTH);
            while (firstReporting.get(Calendar.MONTH) == m && firstReporting.get(Calendar.YEAR) < 2100)
                firstReporting.add(Calendar.DATE, 1);
            firstReporting.add(Calendar.DATE, -1);
            // If we went over Valid To date, rewind and repeat
            //
            if (firstReporting.after(validTo)) {
                firstReporting = rewindDate;
                firstReporting.add(Calendar.MONTH, -freq);
                m = firstReporting.get(Calendar.MONTH);
                while (firstReporting.get(Calendar.MONTH) == m)
                    firstReporting.add(Calendar.DATE, 1);
                firstReporting.add(Calendar.DATE, -1);
            }
        }
        LOGGER.debug("response: " + formatCalendar(firstReporting));
        return firstReporting;
    }
    
    Calendar oldCalculateNextDeadline(String[] deadlines){

        int m;
        int year = Integer.parseInt(deadlines[1].substring(0, 4));
        int month = Integer.parseInt(deadlines[1].substring(5, 7));
        int day = Integer.parseInt(deadlines[1].substring(8));
        int yearTo = Integer.parseInt(deadlines[3].substring(0, 4));
        int monthTo = Integer.parseInt(deadlines[3].substring(5, 7));
        int dayTo = Integer.parseInt(deadlines[3].substring(8));

        GregorianCalendar repDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        repDate.set(year, month - 1, day, 20, 0);
        GregorianCalendar toDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        toDate.set(yearTo, monthTo - 1, dayTo, 23, 59);
        GregorianCalendar currDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        LOGGER.debug("old:  " + formatCalendar(currDate) + " " + formatCalendar(repDate)+ " " + formatCalendar(toDate));

        int freq = Integer.parseInt(deadlines[2]);
        // No point in updating if non-repeating
        //
        currDate.add(Calendar.DATE, -3 * freq);
        if (day < 28) {
            while (repDate.before(currDate) && repDate.before(toDate))
                repDate.add(Calendar.MONTH, freq);
            if (repDate.after(toDate))
                repDate.add(Calendar.MONTH, -freq);
        } else {
            repDate.add(Calendar.DATE, -3);
            while (repDate.before(currDate) && repDate.before(toDate))
                repDate.add(Calendar.MONTH, freq);
            if (repDate.after(toDate))
                repDate.add(Calendar.MONTH, -freq);
            GregorianCalendar rewindDate = (GregorianCalendar) repDate.clone(); // Save for check below
            m = repDate.get(Calendar.MONTH);
            while (repDate.get(Calendar.MONTH) == m)
                repDate.add(Calendar.DATE, 1);
            repDate.add(Calendar.DATE, -1);
            // If we went over Valid To date, rewind and repeat
            //
            if (repDate.after(toDate)) {
                repDate = rewindDate;
                repDate.add(Calendar.MONTH, -freq);
                m = repDate.get(Calendar.MONTH);
                while (repDate.get(Calendar.MONTH) == m)
                    repDate.add(Calendar.DATE, 1);
                repDate.add(Calendar.DATE, -1);
            }
        }

        return repDate;
    }

    private String formatCalendar(Calendar c) {
        return c.get(Calendar.YEAR) + "--" + c.get(Calendar.MONTH) + "--" + c.get(Calendar.DATE) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + "  ";
    }
}