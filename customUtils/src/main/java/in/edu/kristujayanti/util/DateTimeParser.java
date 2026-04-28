package in.edu.kristujayanti.util;


import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import in.edu.kristujayanti.enums.DateFormats;
import jregex.Matcher;
import jregex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DateTimeParser {
    private static final Logger LOGGER = LogManager.getLogger(DateTimeParser.class);
    private static Pattern datePattern;

    public DateTimeParser(DateFormats dateFormat) {
        if (DateFormats.DDMMYYYYHIPHENAVOIDED == dateFormat) {
            datePattern = new Pattern("^({Day}[1-9]|0[1-9]|[12][0-9]|3[01])([/.\\\\])({Month}[1-9]|0[1-9]|1[012])\\2({Year}(19|2[0-1])\\d\\d)$");
        } else if (DateFormats.YYYYMMDD == dateFormat) {
            datePattern = new Pattern("^({Year}(19|2[0-1])\\d\\d)([- /.\\\\])({Month}[1-9]|0[1-9]|1[012])\\3({Day}[1-9]|0[1-9]|[12][0-9]|3[01])$");
        } else if (DateFormats.DDMMYYYY == dateFormat) {
            datePattern = new Pattern("^({Day}[1-9]|0[1-9]|[12][0-9]|3[01])([- /.\\\\])({Month}[1-9]|0[1-9]|1[012])\\2({Year}(19|2[0-1])\\d\\d)$");
        } else if (DateFormats.DDMMYYYYHHMMSS == dateFormat) {
            datePattern = new Pattern("^({Day}[1-9]|0[1-9]|[12][0-9]|3[01])([- /.\\\\])({Month}[1-9]|0[1-9]|1[012])\\2({Year}(19|2[0-1])\\d\\d)\\s+({Time}([0-1]\\d|2[0-3]):[0-5]\\d:[0-5]\\d)$");
        } else if (DateFormats.YYYYMMDDHHMMSS == dateFormat) {
            datePattern = new Pattern("^({Year}(19|2[0-1])\\d\\d)([- /.\\\\])({Month}[1-9]|0[1-9]|1[012])\\3({Day}[1-9]|0[1-9]|[12][0-9]|3[01])\\s+({Time}([0-1]\\d|2[0-3]):[0-5]\\d:[0-5]\\d)$");
        } else if (DateFormats.DDMMYYYYHHMM == dateFormat) {
            datePattern = new Pattern("^({Day}[1-9]|0[1-9]|[12][0-9]|3[01])([- /.\\\\])({Month}[1-9]|0[1-9]|1[012])\\2({Year}(19|2[0-1])\\d\\d)\\s+({Time}([0-1]\\d|2[0-3]):[0-5]\\d)$");
        } else {
            if (DateFormats.YYYYMMDDHHMM != dateFormat) {
                LOGGER.error("The date format {} is null or invalid", dateFormat);
                throw new IllegalArgumentException("The dateFormat argument cannot be null!");
            }

            datePattern = new Pattern("^({Year}(19|2[0-1])\\d\\d)([- /.\\\\])({Month}[1-9]|0[1-9]|1[012])\\3({Day}[1-9]|0[1-9]|[12][0-9]|3[01])\\s+({Time}([0-1]\\d|2[0-3]):[0-5]\\d)$");
        }

    }

    public static ZonedDateTime parseDate(String dateAsString) {
        if (StringUtils.isEmpty(dateAsString)) {
            throw new IllegalArgumentException("Null/Empty date string to parse!");
        } else {
            Matcher ymdMatcher = datePattern.matcher(dateAsString);
            if (ymdMatcher.matches()) {
                int dayOfMonth = Integer.parseInt(ymdMatcher.group("Day"));
                int month = Integer.parseInt(ymdMatcher.group("Month"));
                int year = Integer.parseInt(ymdMatcher.group("Year"));

                ZonedDateTime zonedDateTime;
                try {
                    zonedDateTime = ZonedDateTime.of(year, month, dayOfMonth, 18, 30, 0, 0, ZoneId.of("UTC")).minusDays(1L);
                } catch (DateTimeException var7) {
                    throw new IllegalArgumentException("Date is not valid");
                }

                return zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
            } else {
                throw new IllegalArgumentException("The date string passed: " + dateAsString + " is invalid format!");
            }
        }
    }
}
