package pe.gob.mpfn.casilla.notifications.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {

    private DateUtils() {
    }

    public static boolean isDateOlderThan180Days(Date dateToCheck) {

        LocalDate localDateToCheck = dateToCheck.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentDate = LocalDate.now();
        Duration duration = Duration.between(localDateToCheck.atStartOfDay(), currentDate.atStartOfDay());
        long daysBetween = duration.toDays();

        return daysBetween > 181;
    }
}
