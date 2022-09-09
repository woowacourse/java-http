package utils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ParseUtils {

    private ParseUtils() {
    }

    public static Map<String, String> parse(final String string, final String regex1, final String regex2) {
        return Arrays.stream(string.split(regex1))
                .map(header -> header.split(regex2))
                .collect(Collectors.toMap(header -> header[0], header -> header[1]));
    }
}
