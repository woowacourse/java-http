package org.apache.coyote;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import org.apache.coyote.common.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestGenerator {

    private static final Logger log = LoggerFactory.getLogger(RequestGenerator.class);

    public static Request accept(BufferedReader reader) throws IOException {
        String startLine = reader.readLine();
        String[] token = startLine.split(" ");
        String[] headers = reader.lines().takeWhile(line -> !line.isEmpty()).toArray(String[]::new);
        Request request = new Request(token[0], token[1], token[2], headers, null);
        if ("POST".equals(token[0])) {
            parseFormParameter(reader, request);
        }
        log.info("request: {}", request);
        return request;
    }

    private static void parseFormParameter(BufferedReader reader, Request request) throws IOException {
        String s = request.getHeaders().getOrDefault("Content-Length", "0");
        int contentLength = Integer.parseInt(s);
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        Arrays.stream(requestBody.split("&")).map(param -> param.split("="))
                .forEach(entry -> request.addParameter(entry[0], entry[1]));
    }
}
