package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Controller;
import org.apache.coyote.exception.ResourceNotFoundException;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.ResponseBody;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;

import static org.apache.coyote.http11.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.apache.coyote.http11.HttpStatus.NOT_FOUND;

public class FrontController implements Controller {

    private static final Map<String, Controller> uriToController = new HashMap<>();

    static {
        uriToController.put("/", (req, res) -> {
            res.setStatus(HttpStatus.OK);
            res.setBody(ResponseBody.from("Hello world!"));
        });
        uriToController.put("/login", new LoginController());
        uriToController.put("/register", new RegisterController());
    }

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        final var uri = request.getPath().getValue();
        final var controller = uriToController.get(uri);
        try {
            if (controller != null) {
                controller.service(request, response);
                return;
            }
            response.setStatus(HttpStatus.OK);
            response.setBody(uri);
        } catch (ResourceNotFoundException e) {
            response.setStatus(NOT_FOUND);
            response.setBody("/404.html");
        } catch (Exception e) {
            response.setStatus(INTERNAL_SERVER_ERROR);
            response.setBody("/500.html");
        }
    }

}
