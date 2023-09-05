package nextstep.jwp.servlet;

import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.servlet.exception.NotFoundFileException;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StaticResource;

import java.io.IOException;

public class ControllerAdvice {

    public void handleException(Exception exception, HttpResponse httpResponse) throws IOException {
        if (exception instanceof UserNotFoundException || exception instanceof NotFoundFileException) {
            StaticResource staticResource = StaticResource.of("/404.html");
            ResponseBody responseBody = ResponseBody.of(staticResource.fileToString(), staticResource.getFileExtension());
            httpResponse.setStatusCode(HttpStatusCode.UNAUTHORIZED);
            httpResponse.setResponseBody(responseBody);
        }

        if (exception instanceof RuntimeException) {
            StaticResource staticResource = StaticResource.of("/500.html");
            ResponseBody responseBody = ResponseBody.of(staticResource.fileToString(), staticResource.getFileExtension());
            httpResponse.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR);
            httpResponse.setResponseBody(responseBody);
        }

    }
}
