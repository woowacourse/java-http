package nextstep.jwp.http;

import java.util.ArrayList;
import java.util.Map;

public class PostMethod extends Method {
    private HttpRequest httpRequest;

    public PostMethod(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public String matchFunction() {
        try {
            String request = String.join(" ", httpRequest.getUrl(), httpRequest.getRequestBody());
            Map.Entry<HttpStatus, String> responseEntry = new ArrayList<>(jwpController.mapResponse(request).entrySet()).get(0);
            return makeResponse(responseEntry.getKey(), "text/html", responseEntry.getValue());
        } catch (IllegalArgumentException e) {
            return makeResponse(HttpStatus.NOT_FOUND, "text/html", e.getMessage());
        }
    }
}
