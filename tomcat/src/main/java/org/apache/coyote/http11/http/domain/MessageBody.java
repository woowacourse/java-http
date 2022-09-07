package org.apache.coyote.http11.http.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class MessageBody {

    private final String value;

    public MessageBody(final String value) {
        this.value = value;
    }

    public static MessageBody from(final BufferedReader bufferedReader, final Headers headers) throws IOException {
        int contentLength = Integer.parseInt(headers
                .getValue()
                .getOrDefault("Content-Length", "0"));

        char[] body = new char[contentLength];
        bufferedReader.read(body);

        return new MessageBody(new String(body));
    }

    public static MessageBody emptyBody() {
        return new MessageBody("");
    }

    public int length() {
        return value.getBytes().length;
    }

    public Map<String, String> getParameters() {
        return Arrays.stream(value.split("&"))
                .map(line -> line.split("="))
                .collect(Collectors.toMap(line -> line[0], this::decode));
    }

    private String decode(final String[] line) {
        return URLDecoder.decode(line[1], StandardCharsets.UTF_8);
    }

    public String getValue() {
        return value;
    }
}
