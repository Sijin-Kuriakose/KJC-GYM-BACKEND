package in.edu.kristujayanti.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import in.edu.kristujayanti.enums.DateFormats;
import in.edu.kristujayanti.enums.TimeZoneEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DateUtils {
    private static final Logger LOGGER = LogManager.getLogger(DateUtils.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss.SSSZ");
    private static final DateTimeFormatter DATE_TIME_FORMATTER_TIME_ZONE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss z");

    private DateUtils(){}

    public static Long currentDateTimeInMillis() {
        try {
            return ZonedDateTime.now(ZoneId.systemDefault()) // Get time in system's timezone
                    .toInstant()
                    .toEpochMilli();
        } catch (DateTimeParseException var2) {
            LOGGER.error("DateTimeParseException occurred while checking current date");
            throw new IllegalArgumentException("Date is invalid");
        }
    }

    @Deprecated(forRemoval = true)
    public static Long currentDateTimeInMillisAsia() {
        try {
            LocalDateTime localDateTime = LocalDateTime.now();
            return localDateTime.atZone(ZoneId.of(TimeZoneEnum.ASIA_KOLKATA.getZoneId())).toInstant().toEpochMilli();
        } catch (DateTimeParseException var2) {
            LOGGER.error("DateTimeParseException occurred while checking current date");
            throw new IllegalArgumentException("Date is invalid");
        }
    }

    public static Long currentDateInMillis() {
        try {
            LocalDate localDate = LocalDate.now();  // Gets the current date
            LocalDateTime startOfDay = localDate.atStartOfDay();  // Sets time to midnight (00:00:00)
            return startOfDay.atZone(ZoneId.of(TimeZoneEnum.UTC.getZoneId())).toInstant().toEpochMilli();
        } catch (DateTimeParseException var2) {
            LOGGER.error("DateParseException occurred while checking current date");
            throw new IllegalArgumentException("Date is invalid");
        }
    }

    public static ZonedDateTime labelToDateTime(String dateString) {
        DateTimeParser dateTimeParser = new DateTimeParser(DateFormats.DDMMYYYYHIPHENAVOIDED);
        return DateTimeParser.parseDate(dateString);
    }

    public static Long labelToDateTimeMillis(String dateString) {
        List<DateTimeFormatter> formatters = Arrays.asList(
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
//                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDate localDate = LocalDate.parse(dateString, formatter);
                LocalDateTime dateTime = LocalDateTime.of(localDate, LocalTime.now());
                ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.of("UTC")); // Use IST
                return zonedDateTime.toInstant().toEpochMilli();
            } catch (DateTimeParseException e) {
                LOGGER.debug("Failed to parse '{}' with formatter '{}'", dateString, formatter);
            }
        }
        LOGGER.error("Unable to parse date string: {}", dateString);
        return null; // Return null if parsing fails for all formats
    }

    /**
     * Converts a date string to milliseconds at start of day (00:00:00.000)
     *
     * @param dateString Date string in supported formats
     * @return Milliseconds since epoch for start of day, or null if parsing fails
     */
    public static Long getStartOfDayMillis(String dateString) {
        List<DateTimeFormatter> formatters = Arrays.asList(
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDate localDate = LocalDate.parse(dateString, formatter);
                LocalDateTime startOfDay = localDate.atStartOfDay();
                ZonedDateTime zonedDateTime = startOfDay.atZone(ZoneId.of("UTC"));
                return zonedDateTime.toInstant().toEpochMilli();
            } catch (DateTimeParseException e) {
                LOGGER.debug("Failed to parse '{}' with formatter '{}'", dateString, formatter);
            }
        }
        LOGGER.error("Unable to parse date string: {}", dateString);
        return null;
    }

    /**
     * Converts a date string to milliseconds at end of day (23:59:59.999)
     *
     * @param dateString Date string in supported formats
     * @return Milliseconds since epoch for end of day, or null if parsing fails
     */
    public static Long getEndOfDayMillis(String dateString) {
        List<DateTimeFormatter> formatters = Arrays.asList(
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDate localDate = LocalDate.parse(dateString, formatter);
                LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX); // 23:59:59.999999999
                ZonedDateTime zonedDateTime = endOfDay.atZone(ZoneId.of("UTC"));
                return zonedDateTime.toInstant().toEpochMilli();
            } catch (DateTimeParseException e) {
                LOGGER.debug("Failed to parse '{}' with formatter '{}'", dateString, formatter);
            }
        }
        LOGGER.error("Unable to parse date string: {}", dateString);
        return null;
    }

    public static Long labelToStartDayDateTimeMillis(String dateString) {
        List<DateTimeFormatter> formatters = Arrays.asList(
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDate localDate = LocalDate.parse(dateString, formatter);
                // Set the time to midnight at the start of the specified date
                LocalDateTime dateTime = LocalDateTime.of(localDate, LocalTime.MIDNIGHT);
                ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.of("Asia/Kolkata")); // Use IST
                return zonedDateTime.toInstant().toEpochMilli();
            } catch (DateTimeParseException e) {
                LOGGER.debug("Failed to parse '{}' with formatter '{}'", dateString, formatter);
            }
        }
        LOGGER.error("Unable to parse date string: {}", dateString);
        return null; // Return null if parsing fails for all formats
    }


    public static Instant millisToInstant(Long dateInMillis) {
        return Instant.ofEpochMilli(dateInMillis);
    }

    public static long convertDateTimeToStartOfDayToMillis(long dateInMillis) {
        try {
            // Convert the milliseconds to Instant
            Instant instant = Instant.ofEpochMilli(dateInMillis);

            // Convert Instant to LocalDate in UTC
            LocalDate localDate = instant.atZone(ZoneOffset.UTC).toLocalDate();

            // Convert LocalDate to ZonedDateTime at the start of the day in UTC
            ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneOffset.UTC);

            // Convert ZonedDateTime to epoch milliseconds
            return zonedDateTime.toInstant().toEpochMilli();

        } catch (DateTimeException var2) {
            LOGGER.error("DateTimeException occurred while processing date in millis {} and exception is {} ", dateInMillis, var2);
            throw new IllegalArgumentException("Date " + dateInMillis + " is invalid");
        }
    }

    public static long convertDateTimeToEndOfDayToMillis(long dateInMillis) {
        try {
            // Convert the milliseconds to Instant
            Instant instant = Instant.ofEpochMilli(dateInMillis);

            // Convert Instant to LocalDate in UTC
            LocalDate localDate = instant.atZone(ZoneOffset.UTC).toLocalDate();

            // Convert LocalDate to ZonedDateTime at the end of the day in UTC (23:59:59.999)
            ZonedDateTime zonedDateTime = localDate.atTime(LocalTime.MAX).atZone(ZoneOffset.UTC);

            // Convert ZonedDateTime to epoch milliseconds
            return zonedDateTime.toInstant().toEpochMilli();

        } catch (DateTimeException var2) {
            LOGGER.error("DateTimeException occurred while processing date in millis {} and exception is {} ", dateInMillis, var2);
            throw new IllegalArgumentException("Date " + dateInMillis + " is invalid");
        }
    }


    public static Long convertDateTimeStringToMillis(String dateAsString) {
        // Define supported date formats
        List<DateTimeFormatter> dateFormatters = Arrays.asList(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss.SSSZ")

        );

        for (DateTimeFormatter formatter : dateFormatters) {
            try {
                if (formatter == DateTimeFormatter.ISO_DATE_TIME) {
                    // Handle ISO 8601 format with potential time zones
                    Instant instant = Instant.parse(dateAsString);
                    return instant.toEpochMilli();
                } else {
                    // Parse as LocalDateTime for timezone-less formats
                    LocalDateTime localDateTime = LocalDateTime.parse(dateAsString, formatter);
                    return localDateTime.atZone(ZoneId.of("Asia/Kolkata")).toInstant().toEpochMilli();
                }
            } catch (DateTimeParseException ignored) {
                // Continue trying the next formatter
            }
        }

        // Log error and throw exception if all formats fail
        LOGGER.error("Unable to parse date string: {}", dateAsString);
        throw new IllegalArgumentException("Date " + dateAsString + " is invalid");
    }



    public static long getCurrentTimeInMillis() {
        ZonedDateTime zonedDateTime = Instant.now().atZone(ZoneId.of(TimeZoneEnum.UTC.getZoneId()));
        return zonedDateTime.toInstant().toEpochMilli();
    }


    public static String convertMillisToDateString(Long millis) {
        if (millis == null) {
            throw new IllegalArgumentException("Milliseconds cannot be null.");
        }

        // Convert milliseconds to Instant
        Instant instant = Instant.ofEpochMilli(millis);

        // Convert Instant to ZonedDateTime in IST
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("UTC"));

        // Format the ZonedDateTime to a date string using the specified pattern
        return DATE_FORMATTER.format(zonedDateTime);
    }

    public static String convertMillisToDateTimeString(Long millis) {
        if (millis == null) {
            throw new IllegalArgumentException("Milliseconds cannot be null.");
        }

        // Convert milliseconds to Instant
        Instant instant = Instant.ofEpochMilli(millis);

        // Convert Instant to ZonedDateTime in IST
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of(TimeZoneEnum.UTC.getZoneId()));

        // Format the ZonedDateTime to a date string using the specified pattern
        return DATE_TIME_FORMATTER.format(zonedDateTime);
    }


    public static String convertMillisToDateTimeStringAsia(Long millis) {
        if (millis == null) {
            throw new IllegalArgumentException("Milliseconds cannot be null.");
        }

        // Convert milliseconds to Instant
        Instant instant = Instant.ofEpochMilli(millis);

        // Convert Instant to ZonedDateTime in IST
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of(TimeZoneEnum.ASIA_KOLKATA.getZoneId()));

        // Format the ZonedDateTime to a date string using the specified pattern
        return DATE_TIME_FORMATTER.format(zonedDateTime);
    }

    /**
     * Extracts the time component in milliseconds from the given datetime in milliseconds
     * based on the Asia/Kolkata timezone (Indian Standard Time).
     *
     * @param millis the datetime in milliseconds since the epoch (UTC).
     * @return the time component in milliseconds relative to midnight in Asia/Kolkata timezone.
     */
    public static Long extractTimeMillisFromDateTimeMillisAsia(Long millis) {
        if (millis == null) {
            throw new IllegalArgumentException("Milliseconds cannot be null.");
        }

        // Convert milliseconds to Instant
        Instant instant = Instant.ofEpochMilli(millis);

        // Convert Instant to ZonedDateTime in IST (Indian Standard Time)
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("Asia/Kolkata"));

        // Get the start of the day (midnight) in IST
        ZonedDateTime startOfDay = zonedDateTime.toLocalDate().atStartOfDay(zonedDateTime.getZone());

        // Calculate the difference between the given time and midnight (start of the day)

        // Return the time part in milliseconds
        return millis - startOfDay.toInstant().toEpochMilli();
    }

    public static String convertMillisToDateTimeStringAsiaZone(Long millis) {
        if (millis == null) {
            throw new IllegalArgumentException("Milliseconds cannot be null.");
        }

        // Convert milliseconds to Instant
        Instant instant = Instant.ofEpochMilli(millis);

        // Convert Instant to ZonedDateTime in IST
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of(TimeZoneEnum.ASIA_KOLKATA.getZoneId()));

        // Format the ZonedDateTime to a date string using the specified pattern
        return DATE_TIME_FORMATTER_TIME_ZONE.format(zonedDateTime);
    }


    //return Date time string with out time zone
    public static String convertMillisToDateTimeStringAsiaWithoutZone(Long millis) {
        if (millis == null) {
            throw new IllegalArgumentException("Milliseconds cannot be null.");
        }

        // Convert milliseconds to Instant
        Instant instant = Instant.ofEpochMilli(millis);

        // Convert Instant to ZonedDateTime in IST
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of(TimeZoneEnum.ASIA_KOLKATA.getZoneId()));

        // Format the ZonedDateTime to a date string without the zone name
        DateTimeFormatter formatterWithoutZone = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return zonedDateTime.format(formatterWithoutZone);
    }


    /**
     * Converts a time string (e.g., "8:00", "17:30") to milliseconds since midnight.
     *
     * @param time The time string in "HH:mm" or "H:mm" format.
     * @return The time in milliseconds since midnight.
     */
    public static Long convertTimeToMillis(String time) {
        // Define the formatter to parse the time string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");

        // Parse the time string into a LocalTime object
        LocalTime localTime = LocalTime.parse(time, formatter);

        // Convert the LocalTime to milliseconds since midnight
        return localTime.toSecondOfDay() * 1000L;
    }

    /**
     * Converts a time in milliseconds since midnight to a time string in "HH:mm" format.
     *
     * @param timeInMillis The time in milliseconds since midnight.
     * @return The time string in "HH:mm" format.
     */
    public static String convertTimeMillisToTimeString(Long timeInMillis) {
        if (timeInMillis == null || timeInMillis < 0) {
            throw new IllegalArgumentException("Time in milliseconds must be non-negative and not null.");
        }

        // Convert milliseconds to seconds and then to LocalTime
        LocalTime localTime = LocalTime.ofSecondOfDay(timeInMillis / 1000);

        // Format the LocalTime to "HH:mm" string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return localTime.format(formatter);
    }

    public static long labelToStartDayUnixTimestamp(String date)
    {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .atStartOfDay(ZoneId.of("Asia/Kolkata"))
                .toEpochSecond();
    }

    public static long labelToEndDayUnixTimestamp(String date)
    {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .atTime(LocalTime.MAX)
                .atZone( ZoneId.of("Asia/Kolkata"))
                .toEpochSecond();
    }

    public static String convertMillisToDateStringAsia(Long millis) {
        if (millis == null) {
            throw new IllegalArgumentException("Milliseconds cannot be null.");
        } else {
            Instant instant = Instant.ofEpochMilli(millis);
            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("Asia/Kolkata"));
            return DATE_FORMATTER.format(zonedDateTime);
        }
    }
}