package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class InputStreamConvertor {
    public static String convertToString(InputStream inputStream, Charset charset) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));
        StringBuilder stringBuilder = new StringBuilder();

        while (reader.ready()) {
            stringBuilder.append(reader.readLine())
                    .append(System.lineSeparator());
        }

        return stringBuilder.toString();
    }

    public static List<String> convertToLines(InputStream inputStream, Charset charset) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));
        List<String> lines = new ArrayList<>();

        while (reader.ready()) {
            lines.add(reader.readLine());
        }

        return lines;
    }
}
