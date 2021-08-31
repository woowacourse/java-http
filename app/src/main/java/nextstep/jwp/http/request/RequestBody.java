package nextstep.jwp.http.request;

import java.util.HashMap;

public class RequestBody {

    private HashMap<String, String> requestBodies;

    private RequestBody(HashMap<String, String> requestBodies) {
        this.requestBodies = requestBodies;
    }

    public static RequestBody of(HashMap<String, String> requestBodies) {
        return new RequestBody(requestBodies);
    }

    public String getParameter(String key) {
        return requestBodies.get(key);
    }
}
