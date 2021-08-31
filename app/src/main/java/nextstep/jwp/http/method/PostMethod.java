package nextstep.jwp.http.method;

import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public class PostMethod extends Method {
    private final HttpRequest httpRequest;

    public PostMethod(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public HttpResponse matchFunction() {
        String request = getParams();
        return getHttpResponse(ContentType.NOTHING, jwpController.mapResponse(request));
    }

    private String getParams() {
        if (httpRequest.getRequestBody() == null) {
            return String.join(" ", httpRequest.getUrl());
        }
        return String.join(" ", httpRequest.getUrl(), httpRequest.getRequestBody());
    }
}
