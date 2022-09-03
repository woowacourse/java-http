package org.apache.coyote.http11.model.request;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Parser {

    public static String parse(InputStream inputStream) throws IOException {
        StringBuilder response = new StringBuilder();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, UTF_8));

        // request-line
        response.append(reader.readLine()).append("\n");


        // header
        String temp;
        while (!"".equals(temp = reader.readLine())) {
            response.append(temp).append("\n");
        }

        // body
        while(!"".equals(temp = reader.readLine())) {
            response.append(temp).append("\n");
        }
        return response.toString();
    }
}
