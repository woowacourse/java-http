package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.http.auth.HttpCookie;

public class RequestHeader {
    private final Map<String, String> headers;
    private final HttpCookie httpCookie;

    public RequestHeader(Map<String, String> headers, HttpCookie cookie) {
        this.headers = headers;
        this.httpCookie = cookie;
    }

    public RequestHeader(Map<String, String> headers) {
        this(headers, HttpCookie.EMPTY);
    }

    public static RequestHeader createFromBufferedReader(BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        while (bufferedReader.ready()) {
            String readLine = bufferedReader.readLine();
            String[] splitHeader = readLine.split(": ");
            if (splitHeader[0].equals("")) {
                break;
            }
            headers.put(splitHeader[0], splitHeader[1]);
        }
        if (headers.get("Cookie") != null) {
            return new RequestHeader(headers, HttpCookie.StringOf(headers.get("Cookie")));
        }
        return new RequestHeader(headers);
    }

    public boolean hasHeader(String header) {
        return this.headers.containsKey(header);
    }

    public Integer getBodySize() {
        String contentLength = this.headers.getOrDefault("Content-Length", "0");
        return Integer.parseInt(contentLength.strip());
    }

    public String getHttpCookieId() {
        return httpCookie.getSessionIdToString();
    }
}
