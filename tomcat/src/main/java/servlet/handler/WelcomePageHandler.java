package servlet.handler;

import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;
import org.apache.coyote.response.StatusCode;

public class WelcomePageHandler implements Handler {

    @Override
    public void handleRequest(Request request, Response response) {
        response.configureViewAndStatus("/welcome", StatusCode.OK);
    }
}
