package nextstep.jwp.handler;

import nextstep.jwp.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class Handler {

    private static ExceptionHandler exceptionHandler = new ExceptionHandler();

    public static HttpResponse handle(HttpRequest request) {
        Controller controller = RequestMapping.getController(request);
        HttpResponse response = new HttpResponse();
        try {
            controller.service(request, response);
            return response;
        } catch (DashboardException e) {
            return exceptionHandler.handle(e);
        } catch (Exception e){
            return exceptionHandler.handleInternalServerError();
        }
    }
}
