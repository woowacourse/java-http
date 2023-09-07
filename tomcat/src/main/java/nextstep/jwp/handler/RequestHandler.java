package nextstep.jwp.handler;

import java.io.IOException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public interface RequestHandler {

    HttpResponse handle(HttpRequest request) throws IOException;
}
