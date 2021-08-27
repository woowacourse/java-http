package nextstep.jwp.http.response.headers;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.model.StaticResource;

public class ResponseHeaders {

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
}
