package calendar.utilities;
import calendar.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeConverter {

    static public LocalDateTime convertToTimeZone(String timeZone, LocalDateTime date) {
        ZonedDateTime utcTime = ZonedDateTime.of(date, ZoneId.of(timeZone));
        //ZonedDateTime timeInTimeZone = utcTime.withZoneSameInstant(ZoneId.of(timeZone));
        return utcTime.toLocalDateTime();
    }
}
