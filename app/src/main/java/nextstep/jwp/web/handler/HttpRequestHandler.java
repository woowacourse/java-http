package nextstep.jwp.web.handler;

import java.io.IOException;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.response.HttpResponse;

public interface HttpRequestHandler {

    void handle(HttpRequest request, HttpResponse response) throws IOException;
}
