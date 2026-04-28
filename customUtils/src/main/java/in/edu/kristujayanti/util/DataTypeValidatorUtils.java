package in.edu.kristujayanti.util;

import jregex.Matcher;
import jregex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DataTypeValidatorUtils {
    private static final Logger LOGGER = LogManager.getLogger(DataTypeValidatorUtils.class);
    private static final Pattern NUMERIC_VALIDATOR = new Pattern("^(?:\\s*)((?:[-]?)(?:\\d*)(\\.?)(\\d+))(%?|[lL]?|[dD]?|[sS]?|[fF]?)(?:\\s*)$");
    private static final String[] VALID_BOOLEAN_VALUES = new String[]{"true", "false", "TRUE", "FALSE"};
    private static final Pattern EMAIL_ID_VALID_PATTERN = new Pattern("(([a-z0-9_\\-\\.])+\\@([a-z0-9_\\-\\.])+\\.([a-z0-9-]+))");


    public static int getIntegerValue(String value) {
        if (StringUtils.isNotBlank(value)) {
            String strippedVal = stripCommaWhiteSpace(value);
            Matcher matcher = NUMERIC_VALIDATOR.matcher(strippedVal);
            if (matcher.matches() && StringUtils.isBlank(matcher.group(2)) && StringUtils.isBlank(matcher.group(4))) {
                try {
                    strippedVal = matcher.group(1);
                    return intValueOf(strippedVal);
                } catch (Exception var4) {
                    LOGGER.error("Exception occurred while checking the value {} is integer and the exception is {} ", value, var4);
                    throw new IllegalArgumentException("Invalid integer value " + value);
                }
            }
        }

        throw new IllegalArgumentException("Invalid integer value " + value);
    }

    public static Double getDoubleValue(String value) {
        if (StringUtils.isNotBlank(value)) {
            String strippedVal = stripCommaWhiteSpace(value);
            Matcher matcher = NUMERIC_VALIDATOR.matcher(strippedVal);
            if (matcher.matches()) {
                if (StringUtils.isNotBlank(matcher.group(4)) && !StringUtils.equalsIgnoreCase(matcher.group(4), "d")) {
                    throw new IllegalArgumentException("Invalid double value " + value);
                }

                try {
                    strippedVal = matcher.group(1);
                    return Double.parseDouble(strippedVal);
                } catch (NumberFormatException var4) {
                    LOGGER.error("NumberFormatException occurred while checking the value {} is double and the exception is {} ", value, var4);
                    throw new IllegalArgumentException("Invalid double value " + value);
                }
            }
        }

        throw new IllegalArgumentException("Invalid double value " + value);
    }

    public static Long getLongValue(String value) {
        if (StringUtils.isNotBlank(value)) {
            String strippedVal = stripCommaWhiteSpace(value);
            Matcher matcher = NUMERIC_VALIDATOR.matcher(strippedVal);
            if (matcher.matches() && StringUtils.isBlank(matcher.group(2))) {
                if (StringUtils.isNotBlank(matcher.group(4)) && !StringUtils.equalsIgnoreCase(matcher.group(4), "l")) {
                    throw new IllegalArgumentException("Invalid long value " + value);
                }

                try {
                    strippedVal = matcher.group(1);
                    return Long.parseLong(strippedVal);
                } catch (NumberFormatException var4) {
                    LOGGER.error("NumberFormatException occurred while checking the value {} is Long and the exception is {} ", value, var4);
                    throw new IllegalArgumentException("Invalid long value " + value);
                }
            }
        }

        throw new IllegalArgumentException("Invalid long value " + value);
    }

    public static boolean isValidDate(String dateAsString) {
        try {
            DateUtils.labelToDateTime(dateAsString);
            return true;
        } catch (Exception var2) {
            LOGGER.error("Exception occurred while checking the date value {} is valid and the exception is {} ", dateAsString, var2);
            return false;
        }
    }

    public static boolean isBoolean(String value) {
        if (value != null) {
            String val = value.trim();
            String[] var2 = VALID_BOOLEAN_VALUES;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String validValue = var2[var4];
                if (validValue.equals(val)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static Boolean toBoolean(String value) {
        return Boolean.valueOf(value);
    }

    private static String stripCommaWhiteSpace(String value) {
        return StringUtils.containsAny(value, new char[]{',', ' ', '"'}) ? StringUtils.replaceChars(value, ", \"", "") : value;
    }

    private static int intValueOf(String str) throws NumberFormatException {
        long intVal = 0L;
        boolean sign = str.charAt(0) == '-';
        int idx = sign ? 1 : 0;
        int end = str.length();
        char ch = str.charAt(idx);

        while(true) {
            intVal += (long)(48 - ch);
            ++idx;
            if (idx == end) {
                intVal = sign ? intVal : -intVal;
                if (intVal < -2147483648L) {
                    LOGGER.error("The int value {} is less than the minimum Integer value", intVal);
                    throw new NumberFormatException("The string:" + str + " underflow's integer range min: -2147483648!");
                } else if (intVal >= 2147483647L) {
                    LOGGER.error("The int value {} is greater than maximum Integer value", intVal);
                    throw new NumberFormatException("The string:" + str + " overflow's integer range max: 2147483647!");
                } else {
                    return (int)intVal;
                }
            }

            ch = str.charAt(idx);
            intVal *= 10L;
        }
    }
}

