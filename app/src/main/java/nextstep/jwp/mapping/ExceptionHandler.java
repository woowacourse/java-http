package nextstep.jwp.mapping;

import java.io.IOException;
import nextstep.jwp.FileAccess;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.content.ContentType;
import nextstep.jwp.http.response.status.HttpStatus;

public class ExceptionHandler {

    private ExceptionHandler() {
    }

    public static void handle(HttpStatus httpStatus, HttpRequest httpRequest,
            HttpResponse httpResponse) throws IOException {
        String path = "/" + httpStatus.getValue() + ".html";

        String resource = new FileAccess(path).getFile();

        httpResponse.setStatusLine(httpRequest.getProtocolVersion(), httpStatus);
        httpResponse.addResponseHeader("Content-Type", ContentType.HTML.getType());
        httpResponse.addResponseHeader("Content-Length", String.valueOf(resource.getBytes().length));
        httpResponse.setResponseBody(resource);
    }
}
