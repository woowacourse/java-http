package nextstep.jwp.handler;

import nextstep.jwp.controller.Controller;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpResponseParser;

public class Handler {

    private static final RequestHandler requestHandler = new RequestHandler(new SessionManager());

    public static String handle(HttpRequest request) {
        Controller controller = RequestMapping.getController(request);
        HttpResponse response = new HttpResponse();
        try {
            controller.service(request, response);
            return HttpResponseParser.parse(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
