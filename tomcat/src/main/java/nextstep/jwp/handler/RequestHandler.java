package nextstep.jwp.handler;

import java.io.IOException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public interface RequestHandler {
    HttpResponse handle(HttpRequest request) throws IOException;
}
