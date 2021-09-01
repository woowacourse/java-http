package nextstep.joanne.server.http.request;

import java.util.HashMap;
import java.util.Map;

public class RequestHeaders {
    private final HashMap<String, String> headers;

    public RequestHeaders(Map<String, String> headers) {
        this.headers = (HashMap<String, String>) headers;
    }

    public boolean containsResource(String staticResource) {
        return headers.containsKey("Accept") && headers.get("Accept").contains(staticResource);
    }
}
