package servlet.handler;

import org.apache.coyote.http.StatusCode;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.Response;

public class WelcomeHandler implements Handler {

    private static WelcomeHandler INSTANCE;

    private WelcomeHandler() {
    }

    public static WelcomeHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WelcomeHandler();
        }
        return INSTANCE;
    }

    @Override
    public void handleRequest(Request request, Response response) {
        response.configureViewAndStatus("/welcome", StatusCode.OK);
    }
}
