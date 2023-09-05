package nextstep.jwp.servlet;

import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.servlet.exception.NotFoundFileException;
import org.apache.catalina.servlet.exception.MethodNotAllowedException;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StaticResource;

public class ControllerAdvice {

    public void handleException(Exception exception, HttpResponse httpResponse) {
        if (exception instanceof UserNotFoundException) {
            StaticResource staticResource = StaticResource.of("/401.html");
            ResponseBody responseBody = ResponseBody.of(staticResource.fileToString(), staticResource.getFileExtension());
            httpResponse.setStatusCode(HttpStatusCode.UNAUTHORIZED);
            httpResponse.setResponseBody(responseBody);
            return;
        }
        if (exception instanceof NotFoundFileException) {
            StaticResource staticResource = StaticResource.of("/404.html");
            ResponseBody responseBody = ResponseBody.of(staticResource.fileToString(), staticResource.getFileExtension());
            httpResponse.setStatusCode(HttpStatusCode.NOT_FOUND);
            httpResponse.setResponseBody(responseBody);
            return;
        }
        if (exception instanceof MethodNotAllowedException) {
            ResponseBody responseBody = ResponseBody.of("Method Not allowed", "html");
            httpResponse.setStatusCode(HttpStatusCode.METHOD_NOT_ALLOWED);
            httpResponse.setResponseBody(responseBody);
            return;
        }
        if (exception instanceof RuntimeException) {
            StaticResource staticResource = StaticResource.of("/500.html");
            ResponseBody responseBody = ResponseBody.of(staticResource.fileToString(), staticResource.getFileExtension());
            httpResponse.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR);
            httpResponse.setResponseBody(responseBody);
        }

    }
}
