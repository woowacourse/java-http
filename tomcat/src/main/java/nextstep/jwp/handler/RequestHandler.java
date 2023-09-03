package nextstep.jwp.handler;

import org.apache.catalina.servlet.request.HttpRequest;
import org.apache.catalina.servlet.response.HttpResponse;

public interface RequestHandler {

    boolean canHandle(HttpRequest request);

    HttpResponse handle(HttpRequest request);
}
