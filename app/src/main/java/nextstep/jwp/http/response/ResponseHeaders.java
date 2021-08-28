package nextstep.jwp.http.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import nextstep.jwp.model.StaticResource;

public class ResponseHeaders {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private final Map<String, String> headers;

    public ResponseHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static ResponseHeaders from(StaticResource staticResource) {
        HashMap<String, String> headers = new HashMap<>();

        headers.put("Content-Type", staticResource.getContentType());
        headers.put("Content-Length", staticResource.getContentLength());

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
