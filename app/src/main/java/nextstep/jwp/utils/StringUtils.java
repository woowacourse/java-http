package nextstep.jwp.utils;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class StringUtils {

    private static final String BLANK = " ";
    private static final String NEW_LINE = "\r\n";

    private StringUtils() {
    }

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

    public static Map<String, String> extractMap(String s, String pieceSeparator, String paramSeparator) {
        validateSeparator(pieceSeparator);
        validateSeparator(paramSeparator);
        if (s.isBlank()) {
            return new LinkedHashMap<>();
        }
        String[] pieces = s.trim().split(pieceSeparator);
        return Arrays.stream(pieces)
                .map(piece -> extractParam(piece, paramSeparator))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k, v) -> k, LinkedHashMap::new));
    }

    private static void validateSeparator(String separator) {
        if (Objects.isNull(separator) || separator.isEmpty()) {
            throw new IllegalArgumentException("separator 가 없거나 빈 문자열이면 안 됩니다.");
        }
    }

    private static Map.Entry<String, String> extractParam(String piece, String paramSeparator) {
        int index = piece.indexOf(paramSeparator);
        String key = piece.substring(0, index).trim();
        String value = piece.substring(index + 1).trim();
        return new AbstractMap.SimpleEntry<>(key, value);
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
