package nextstep.jwp.http.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import nextstep.jwp.model.StaticResource;

public class ResponseHeaders {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private final Map<String, String> headers;

    private ResponseHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static ResponseHeaders ofBody(StaticResource staticResource) {
        Map<String, String> headers = new HashMap<>();

        headers.put("Content-Type", staticResource.getContentType());
        headers.put("Content-Length", staticResource.getContentLength());

        return new ResponseHeaders(headers);
    }

    public static ResponseHeaders ofRedirect(String location) {
        Map<String, String> headers = new HashMap<>();

        headers.put("Location", location);

        return new ResponseHeaders(headers);
    }

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(NEW_LINE);

        for (Entry<String, String> entry : headers.entrySet()) {
            stringJoiner.add(String.format("%s: %s ", entry.getKey(), entry.getValue()));
        }

        return stringJoiner.toString();
    }
}
