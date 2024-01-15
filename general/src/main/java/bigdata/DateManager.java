package bigdata;

import java.time.*;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class DateManager {
    public static long getYear(Instant date){
        return date.atZone(ZoneId.of("UTC")).getYear();
    }

    public static long getMonth(Instant date){
        return date.atZone(ZoneId.of("UTC")).getMonth().getValue();
    }

    public static long getWeek(Instant date){
        return date.atZone(ZoneId.of("UTC")).get(WeekFields.ISO.weekOfWeekBasedYear());
    }

    public static Instant getDateFromWeek(int year, int week){
        LocalDate weekDate = LocalDate.ofYearDay(year, 2)
                .with(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear(), week);
        return ZonedDateTime.of(weekDate, LocalTime.MIDNIGHT, ZoneOffset.UTC).toInstant();
    }

    public static  Instant getDateFromMonth(int year, int month){
        LocalDate localDate = LocalDate.parse(year + "-" + ((month < 10) ? "0" : "") + month + "-01");
        return ZonedDateTime.of(localDate, LocalTime.MIDNIGHT, ZoneOffset.UTC).toInstant();
    }

    public static boolean isSameDate(Instant date1, Instant date2, SummaryDateType dateType){
        boolean isSameYear = getYear(date1) == getYear(date2);
        boolean isSameMonth = getMonth(date1) == getMonth(date2);
        boolean isSameWeek = getWeek(date1) == getWeek(date2);
        switch (dateType){
            case NONE:
                return true;
            case MONTHLY:
                return isSameYear && isSameMonth;
            case WEEKLY:
                return  isSameYear && isSameWeek;
            default:
                return false;
        }
    }
}
