package nextstep.jwp.http.response;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.model.StaticResource;

public class ResponseHeaders {

    private final Map<String, String> headers;

    public ResponseHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static ResponseHeaders createWithBody(final StaticResource staticResource) {
        Map<String, String> headers = new HashMap<>();

        headers.put("Content-Type", staticResource.getContentType());
        headers.put("Content-Length", staticResource.getContentLength());

        return new ResponseHeaders(headers);
    }

    public static ResponseHeaders createWithDirect(final String location) {
        Map<String, String> headers = new HashMap<>();

        headers.put("Location", location);

        return new ResponseHeaders(headers);
    }

    public String getHeader(String parameter) {
        return headers.get(parameter);
    }
}
