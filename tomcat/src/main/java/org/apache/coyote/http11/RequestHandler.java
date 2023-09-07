package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Controller;

public class RequestHandler implements Controller {

    private static final Map<String, Controller> uriToController = new HashMap();

    static {
        uriToController.put("/", (req, res) -> {
            res.setStatus(HttpStatus.OK);
            res.setBody(ResponseBody.from("Hello world!"));
        });
        uriToController.put("/login", new LoginController());
        uriToController.put("/register", new RegisterController());
    }

    private final ResourceResponseHandler resourceResponseHandler;

    public RequestHandler() {
        this.resourceResponseHandler = new ResourceResponseHandler();
    }


    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        final var uri = request.getPath().getValue();
        final var controller = uriToController.get(uri);
        if (controller != null) {
            controller.service(request, response);
            return;
        }
        try {
            final var body = resourceResponseHandler.buildBodyFrom(uri);
            response.setStatus(HttpStatus.OK);
            response.setBody(body);
        } catch (Exception e) {
            final var notFoundResponse = resourceResponseHandler.buildBodyFrom("/404.html");
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setBody(notFoundResponse);
        }
    }

}
