package nextstep.jwp.utils;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {

    private static final String BLANK = " ";
    private static final String NEW_LINE = "\r\n";

    public static List<String> splitWithSeparator(String s, String separator) {
        return Arrays
                .stream(s.split(separator))
                .collect(Collectors.toList());
    }

    public static List<String> splitTwoPiecesWithSeparator(String s, String separator) {
        return Arrays
                .stream(s.split(separator, 2))
                .collect(Collectors.toList());
    }

    public static String joinWithBlank(String... words) {
        return String.join(BLANK, words);
    }

    public static String concatNewLine(String s) {
        return s.concat(NEW_LINE);
    }

    public static String decode(String s, Charset charset) {
        return URLDecoder.decode(s, charset);
    }
}
