package nextstep.joanne.http.request;

import java.util.HashMap;

public class RequestHeaders {
    private final HashMap<String, String> headers;

    public RequestHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public boolean containsResource(String staticResource) {
        return headers.containsKey("Accept") && headers.get("Accept").contains(staticResource);
    }
}
