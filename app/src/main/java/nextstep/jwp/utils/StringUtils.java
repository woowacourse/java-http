package nextstep.jwp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {

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

    public static String convertToString(BufferedReader bufferedReader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        while (bufferedReader.ready()) {
            stringBuilder.append(bufferedReader.readLine())
                    .append("\r\n");
        }
        return stringBuilder.toString();
    }
}
