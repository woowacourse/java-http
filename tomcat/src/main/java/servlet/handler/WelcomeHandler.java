package servlet.handler;

import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.Response;
import org.apache.coyote.http.StatusCode;

public class WelcomeHandler implements Handler {

    @Override
    public void handleRequest(Request request, Response response) {
        response.configureViewAndStatus("/welcome", StatusCode.OK);
    }
}
