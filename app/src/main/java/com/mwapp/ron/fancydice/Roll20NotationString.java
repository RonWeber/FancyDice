package com.mwapp.ron.fancydice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to parse roll20 notation.
 */
public class Roll20NotationString {
    private int numDice;
    private int numSides;
    private int modifier;
    private boolean dropWasSpecified;
    private int dropLow;
    private int dropHigh;

    //Regex usage example on next line, aligned with relevant parts of the regex; rolls 4d6, drop lowest, +1.  Some of these are optional.  _ marks allowable whitespace
    //"4d6dl1 + 1" ->                                    _   4   d  6             dl         1     _      +     _   1       _
    private static final String GRAND_ROLL20_REGEX = "^\\s*(\\d+)d(\\d+)((d|k|dl|kh|dh|kl)(\\d*))?\\s*(([+-])\\s*(\\d+))?\\s*$";
    private static final int NUMDICE_CAPTURE_GROUP = 1;
    private static final int NUMSIDES_CAPTURE_GROUP = 2;
    //Capture group 3 will be "dl1."  Not useful.
    private static final int DROP_KEEP_MODE_CAPTURE_GROUP = 4; //Nullable
    private static final int DROP_KEEP_NUMBER_CAPTURE_GROUP = 5; //Nullable
    //Capture group 6 will be "+ 1"
    private static final int PLUS_MINUS_CAPTURE_GROUP = 7; //Nullable
    private static final int PLUS_MINUS_VALUE_CAPTURE_GROUP = 8; //Nullable

    private static final Pattern grandRoll20RegexPattern = Pattern.compile(GRAND_ROLL20_REGEX);

    public Roll20NotationString(String source) throws InvalidNotationException {
        parseString(source);
    }

    private void parseString(String source) throws InvalidNotationException {
        Matcher matcher = grandRoll20RegexPattern.matcher(source);
        if (!matcher.matches()) throw new InvalidNotationException();
        parseCoreStuff(matcher);
        parseDropKeep(matcher);
        parseModifier(matcher);
    }

    private void parseCoreStuff(Matcher matcher) {
        numDice = Integer.parseInt(matcher.group(NUMDICE_CAPTURE_GROUP));
        numSides = Integer.parseInt(matcher.group(NUMSIDES_CAPTURE_GROUP));
    }

    private void parseDropKeep(Matcher matcher) {
        dropLow = dropHigh = 0; //In case the groups are null.
        String dropKeepNumberStr = matcher.group(DROP_KEEP_NUMBER_CAPTURE_GROUP);
        int dropKeepNumber = (dropKeepNumberStr == null || dropKeepNumberStr.isEmpty()) ? 1 : Integer.parseInt(dropKeepNumberStr);
        String dropKeepMode = matcher.group(DROP_KEEP_MODE_CAPTURE_GROUP);
        if (dropKeepMode != null) {
            dropWasSpecified = true;
            switch (dropKeepMode) {
                case "d": //Drop lowest
                case "dl":
                    dropLow = dropKeepNumber;
                    break;
                case "k": //Keep highest
                case "kh":
                    dropLow = numDice - dropKeepNumber;
                    break;
                case "dh": //Drop highest
                    dropHigh = dropKeepNumber;
                    break;
                case "kl": //Keep lowest
                    dropHigh = numDice - dropKeepNumber;
                    break;
            }
        } else {
            dropWasSpecified = false;
        }
    }

    private void parseModifier(Matcher matcher) {
        String plusOrMinus = matcher.group(PLUS_MINUS_CAPTURE_GROUP);
        if (plusOrMinus == null) {
            modifier = 0;
            return;
        }
        int plusMinusOne = plusOrMinus.equals("-") ? -1 : 1;
        modifier = Integer.parseInt(matcher.group(PLUS_MINUS_VALUE_CAPTURE_GROUP)) * plusMinusOne;
    }

    public int getNumDice() {
        return numDice;
    }

    public int getNumSides() {
        return numSides;
    }

    public int getModifier() {
        return modifier;
    }

    public boolean dropWasSpecified() {
        return dropWasSpecified;
    }

    public int getDropLow() {
        return dropLow;
    }

    public int getDropHigh() {
        return dropHigh;
    }

    public class InvalidNotationException extends Exception { //Checked.  Because it's probably gonna happen.
        public InvalidNotationException() { super(); }
        public InvalidNotationException(String message) { super(message); }
        public InvalidNotationException(String message, Throwable cause) { super(message, cause); }
        public InvalidNotationException(Throwable cause) { super(cause); }
    }
}