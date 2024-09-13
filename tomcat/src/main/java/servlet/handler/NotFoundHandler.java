package servlet.handler;

import org.apache.coyote.http.StatusCode;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.Response;

public class NotFoundHandler implements Handler {

    private static NotFoundHandler INSTANCE;

    private NotFoundHandler() {
    }

    public static NotFoundHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NotFoundHandler();
        }
        return INSTANCE;
    }

    @Override
    public void handleRequest(Request request, Response response) {
        response.configureViewAndStatus("/404", StatusCode.NOT_FOUND);
    }
}
