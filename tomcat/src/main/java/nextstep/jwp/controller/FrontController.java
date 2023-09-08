package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.ResourceResponseBuilder;
import org.apache.coyote.http11.ResponseBody;

import static org.apache.coyote.http11.HttpStatus.INTERNAL_SERVER_ERROR;

public class FrontController implements Controller {

    private static final Map<String, Controller> uriToController = new HashMap();

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
        } catch (Exception e) {
            response.setStatus(INTERNAL_SERVER_ERROR);
            response.setBody("/500.html");
        }
    }

}
