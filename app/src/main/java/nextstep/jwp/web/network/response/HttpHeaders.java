package nextstep.jwp.web.network.response;

import nextstep.jwp.web.exception.InputException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders of(BufferedReader bufferedReader) {
        try {
            final Map<String, String> headers = new HashMap<>();
            String line = bufferedReader.readLine();
            while (!"".equals(line)) {
                final String[] keyValue = line.split(":");
                headers.put(keyValue[0].trim(), keyValue[1].trim());
                line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
            }
            return new HttpHeaders(headers);
        } catch (IOException exception) {
            throw new InputException("Exception while reading headers from http request.");
        }
    }

    public String get(String key) {
        return headers.getOrDefault(key, "");
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }
}
