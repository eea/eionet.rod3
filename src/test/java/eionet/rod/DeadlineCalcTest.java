package eionet.rod;


import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class DeadlineCalcTest {

    /**
     * Compares the current implementation results with the previous one
     */
    @Test
    public void compareWithOld(){
       DeadlineCalc dc = new DeadlineCalc();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        int target = 5;

       for(int validToDay = 0; validToDay < target*20; validToDay +=target) {
           for (int firstReportingDay = -target*20; firstReportingDay < -target; firstReportingDay+=target) {
               for (int freq = 1; freq < 13; freq++) {
                   Calendar currDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
                   Calendar firstReporting = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
                   Calendar validTo = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
                   firstReporting.add(Calendar.DATE, firstReportingDay);
                   validTo.add(Calendar.DATE, validToDay);

                   Calendar newValue = dc.calculateNextDeadline(currDate, firstReporting, validTo, freq);
                   String[] oldParams = new String[10];
                   // SELECT PK_RA_ID, FIRST_REPORTING, REPORT_FREQ_MONTHS, VALID_TO, TERMINATE "
                   //           0           1               2                3           4
                   oldParams[2] = freq + "";
                   oldParams[1] = sdf.format(firstReporting.getTime());
                   oldParams[3] = sdf.format(validTo.getTime());
                   // currDate internally created

                   Calendar oldValue = dc.oldCalculateNextDeadline(oldParams);
                   System.out.println("Old value:" + oldValue.getTime() + " New value: " + newValue.getTime());
                   assertEquals(newValue.get(Calendar.YEAR), oldValue.get(Calendar.YEAR));
                   assertEquals(sdf.format(newValue.getTime()), sdf.format(oldValue.getTime()));

               }
           }
       }

    }

}
