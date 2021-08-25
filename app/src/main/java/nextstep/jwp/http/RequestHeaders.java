package nextstep.jwp.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class RequestHeaders {
    private final String headers;
    private final String httpMethod;
    private final String uri;
    private final Map<String, String> params;

    public RequestHeaders(InputStream inputStream) throws IOException {
        this.headers = readHeader(inputStream);
        this.httpMethod = extractMethod(headers);
        this.uri = extractUri(headers);
        this.params = new HashMap<>();
    }

    private String readHeader(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuilder request = new StringBuilder();
        while (bufferedReader.ready()) {
            final String line = bufferedReader.readLine();

            if (line == null) {
                return "";
            }

            if ("".equals(line)) {
                return request.toString();
            }

            request.append(line)
                    .append("\r\n");

        }
        return request.toString();
    }

    private String extractMethod(String input) {
        String[] values = input.split(" ");
        if (values.length < 1) {
            return "";
        }
        return input.split(" ")[0];
    }

    private String extractUri(String input) {
        String[] values = input.split(" ");
        if (values.length < 2) {
            return "";
        }
        return input.split(" ")[1];
    }

    public Boolean isQueryString() {
        if (uri.contains("?")) {
            extractQueryString(uri);
            return true;
        }
        return false;
    }

    private Map<String, String> extractQueryString(String uri) {
        int index = uri.indexOf("?");
        String path = uri.substring(0, index);
        String queryString = uri.substring(index + 1);
        String[] queries = queryString.split("&");
        for (String query : queries) {
            String[] split = query.split("=");
            this.params.put(split[0], split[1]);
        }
        return this.params;
    }

    public String getUri() {
        return this.uri;
    }

    public Map<String, String> getParams() {
        return this.params;
    }
}
