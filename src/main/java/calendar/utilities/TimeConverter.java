package calendar.utilities;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class TimeConverter {

    static public LocalDateTime convertToUtc(LocalDateTime time, ZoneId zone) {
        return time.atZone(ZoneOffset.UTC).withZoneSameInstant(zone).toLocalDateTime();
    }
}
