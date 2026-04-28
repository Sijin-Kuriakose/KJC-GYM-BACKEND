package in.edu.kristujayanti.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.TimeZone;

import in.edu.kristujayanti.enums.DateFormats;
import in.edu.kristujayanti.enums.TimeZoneEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

    public final class DateTimeUtil {
        private static final Logger LOGGER = LogManager.getLogger(DateTimeUtil.class);

        public static ZonedDateTime labelToDateTime(String dateString) {
            DateTimeParser dateTimeParser = new DateTimeParser(DateFormats.DDMMYYYYHIPHENAVOIDED);
            return DateTimeParser.parseDate(dateString);
        }

        public static Long convertDateTimeStringToMillis(String dateAsString) {
            try {
                LocalDateTime localDateTime = LocalDateTime.parse(dateAsString, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                return localDateTime.atZone(ZoneId.of(TimeZoneEnum.ASIA_KOLKATA.getZoneId())).toInstant().toEpochMilli();
            } catch (DateTimeParseException var2) {
                LOGGER.error("DateTimeParseException occurred while checking string date value {} is valid and exception is {} ", dateAsString, var2);
                throw new IllegalArgumentException("Date " + dateAsString + " is invalid");
            }
        }

        public static String getCurrentDateTime() {
            return Instant.now().atZone(ZoneId.of(TimeZoneEnum.UTC.getZoneId())).format(DateTimeFormatter.ofPattern("dd MMM yyyy kk:mm:ss"));
        }

        public static long getCurrentTimeInMillis() {
            ZonedDateTime zonedDateTime = Instant.now().atZone(ZoneId.of(TimeZoneEnum.UTC.getZoneId()));
            return zonedDateTime.toInstant().toEpochMilli();
        }

        public static String getDateAsString(TimeZoneEnum timeZone, String format, Long dateInMillis) {
            DateFormat formatter = new SimpleDateFormat(format);
            formatter.setTimeZone(TimeZone.getTimeZone(timeZone.getZoneId()));
            return formatter.format(new Date(dateInMillis));
        }

        public static long zonedDateTimeToMillis(ZonedDateTime zonedDateTime) {
            return zonedDateTime.toInstant().toEpochMilli();
        }

        public static ZonedDateTime millisToZonedDateTime(Long dateInMillis, TimeZoneEnum timeZone) {
            ZoneId zoneId = ZoneId.of(timeZone.getZoneId());
            Instant instant = Instant.ofEpochMilli(dateInMillis);
            return instant.atZone(zoneId);
        }

        public static long dateInMillisPlusDays(Long dateInMillis, Long days) {
            return Instant.ofEpochMilli(dateInMillis).atZone(ZoneId.of(TimeZoneEnum.UTC.getZoneId())).plusDays(days).toInstant().toEpochMilli();
        }

        public static long getStartTimeOfDayInMillis(Long dateInMillis) {
            Instant instant = Instant.ofEpochMilli(dateInMillis);
            ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.of(TimeZoneEnum.ASIA_KOLKATA.getZoneId()));
            ZonedDateTime startOfDay = zonedDateTime.toLocalDate().atStartOfDay(zonedDateTime.getZone());
            return startOfDay.toInstant().toEpochMilli();
        }

        public static ZonedDateTime plusDays(ZonedDateTime dt, int numberOfDaysToAdd) {
            return dt.plusDays((long)numberOfDaysToAdd);
        }

        public static ZonedDateTime toDateTime(long dateInMillis) {
            Instant instant = Instant.ofEpochMilli(dateInMillis);
            return ZonedDateTime.ofInstant(instant, ZoneId.of("UTC"));
        }

        public static ZonedDateTime currentZoneDateTime() {
            return Instant.now().atZone(ZoneId.of(TimeZoneEnum.UTC.getZoneId()));
        }

        public static String getDayTimeDuration(long timestamp1, long timestamp2) {
            LocalDateTime time1 = LocalDateTime.ofEpochSecond(timestamp1 / 1000L, 0, ZoneOffset.UTC);
            LocalDateTime time2 = LocalDateTime.ofEpochSecond(timestamp2 / 1000L, 0, ZoneOffset.UTC);
            Duration duration = Duration.between(time1, time2);
            long hours = duration.toHours();
            duration = duration.minusHours(hours);
            long minutes = duration.toMinutes();
            return String.format("%02d:%02d", hours, minutes);
        }

        public static long convertCurrentDateToMillis() {
            LocalDate currentDate = LocalDate.now();
            Instant instant = currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
            return instant.toEpochMilli();
        }

        public static ZonedDateTime minusDays(ZonedDateTime dt, int numberOfDaysToSubtract) {
            return dt.minusDays((long)numberOfDaysToSubtract);
        }

        public static long getEndTimeOfDayInMillis(Long dateInMillis) {
            Instant instant = Instant.ofEpochMilli(dateInMillis);
            ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.of(TimeZoneEnum.ASIA_KOLKATA.getZoneId()));
            ZonedDateTime endOfDay = zonedDateTime.toLocalDate().atStartOfDay(zonedDateTime.getZone()).plusDays(1L);
            return endOfDay.toInstant().toEpochMilli();
        }
    }
