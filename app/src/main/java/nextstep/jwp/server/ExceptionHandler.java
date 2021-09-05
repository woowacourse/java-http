package nextstep.jwp.server;

import nextstep.jwp.exception.UnAuthorizedException;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpResponseStatus;

import java.io.IOException;

public class ExceptionHandler {
    private static ExceptionHandler instance;

    private ExceptionHandler() {
    }

    public static ExceptionHandler getInstance() {
        if (instance == null) {
            instance = new ExceptionHandler();
        }
        return instance;
     }

    public void handle(HttpResponse httpResponse, Exception e) throws IOException {
        if (e instanceof UnAuthorizedException) {
            httpResponse.flush();
            httpResponse.status(HttpResponseStatus.FOUND);
            httpResponse.location("/401.html");
        }
    }
}
