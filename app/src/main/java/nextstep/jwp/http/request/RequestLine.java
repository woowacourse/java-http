package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestLine {
    private final Map<String, String> headers;

    public RequestLine(Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestLine createFromBufferedReader(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        return RequestLine.createFromPlainText(requestLine);
    }

    public static RequestLine createFromPlainText(String requestLine) {
        Map<String, String> headers = new HashMap<>();
        String[] splitRequestLine = requestLine.split(" ");
        headers.put("httpMethod", splitRequestLine[0]);
        headers.put("uri", splitRequestLine[1]);
        headers.put("httpVersion", splitRequestLine[2]);
        return new RequestLine(headers);
    }

    public String get(String keyName) {
        return this.headers.get(keyName);
    }

    public Boolean isEmpty() {
        return headers.isEmpty();
    }

    public String getMethod() {
        return headers.get("httpMethod");
    }
}
