package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private String method;
    private String path;
    private String protocol;
    private HttpCookie cookies;
    private final Map<String, String> headers = new HashMap<>();
    private Map<String, String> queryParameters = new HashMap<>();
    private String body;

    public HttpRequest(BufferedReader reader) throws IOException {
        List<String> input = getInput(reader);

        String[] split = input.getFirst().split(" ");
        method = split[0];
        protocol = split[2];
        parseUri(split[1]);
        parseHeaders(input.subList(1, input.size()));
        parseBody(reader);
    }

    public Map<String, String> parseQueryStringForm(String queryString) {
        Map<String, String> map = new HashMap<>();
        String[] parameters = queryString.split("&");

        Arrays.stream(parameters)
                .forEach(parameter -> {
                            String[] split = parameter.split("=");
                            map.put(split[0], split[1]);
                        }
                );

        return map;
    }

    private List<String> getInput(BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            lines.add(line);
        }
        return lines;
    }

    private void parseUri(String uri) {
        int index = uri.indexOf("?");
        if (index == -1) {
            path = uri;
            return;
        }
        path = uri.substring(0, index);
        queryParameters = parseQueryStringForm(uri.substring(index + 1));
    }

    private void parseHeaders(List<String> headerString) {
        if (headerString.isEmpty()) {
            return;
        }

        headerString.forEach(
                header -> {
                    int index = header.indexOf(": ");
                    if (header.substring(0, index).equals("Cookie")) {
                        cookies = new HttpCookie(header.substring(index + 1).trim());
                        return;
                    }

                    headers.put(
                            header.substring(0, index).trim(),
                            header.substring(index + 1).trim()
                    );
                }
        );
    }

    private void parseBody(BufferedReader reader) throws IOException {
        String contentLengthValue = headers.get("Content-Length");
        if (contentLengthValue == null) {
            return;
        }
        int contentLength = Integer.parseInt(contentLengthValue.trim());
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        this.body = new String(buffer);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getBody() {
        return body;
    }

    public boolean existsCookie(String key) {
        return cookies != null && cookies.contains(key);
    }
}
