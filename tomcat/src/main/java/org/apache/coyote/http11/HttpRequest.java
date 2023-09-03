package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String target;
    private final Map<String, String> header;
    private final Map<String, String> body;

    private HttpRequest(final String method, final String target, final Map<String, String> header, final Map<String, String> body) {
        this.method = method;
        this.target = target;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest of(final BufferedReader reader) {
        final String method;
        final String target;
        final Map<String, String> header = new HashMap<>();
        final Map<String, String> body = new HashMap<>();

        try {
            final String request = reader.readLine();
            method = request.split(" ")[0];
            target = request.split(" ")[1];

            String line;
            while (!"".equals(line = reader.readLine())) {
                String[] value = line.split(": ");
                header.put(value[0], value[1]);
            }


            if (header.get("Content-Length") != null) {
                int contentLength = Integer.parseInt(header.get("Content-Length"));
                char[] buffer = new char[contentLength];
                reader.read(buffer, 0, contentLength);

                for (String temp : new String(buffer).split("&")) {
                    String[] value = temp.split("=");
                    body.put(value[0], URLDecoder.decode(value[1], "UTF-8"));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new HttpRequest(method, target, header, body);
    }

    public String getMethod() {
        return method;
    }

    public String getTarget() {
        return target;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public Map<String, String> getBody() {
        return body;
    }
}
