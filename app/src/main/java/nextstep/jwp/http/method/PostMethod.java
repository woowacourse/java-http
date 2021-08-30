package nextstep.jwp.http.method;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;

import java.util.ArrayList;
import java.util.Map;

public class PostMethod extends Method {
    private final HttpRequest httpRequest;

    public PostMethod(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public HttpResponse matchFunction() {
        try {
            String request = String.join(" ", httpRequest.getUrl(), httpRequest.getRequestBody());
            Map.Entry<HttpStatus, String> responseEntry = new ArrayList<>(jwpController.mapResponse(request).entrySet()).get(0);
            return new HttpResponse(responseEntry.getKey(), "text/html", responseEntry.getValue());
        } catch (IllegalArgumentException e) {
            return new HttpResponse(HttpStatus.NOT_FOUND, "text/html", e.getMessage());
        }
    }
}
