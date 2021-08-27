package nextstep.jwp.handler;

import java.io.IOException;
import nextstep.jwp.model.Request;

public class NotFoundHandler extends AbstractHandler {

    @Override
    public String message(Request request) throws IOException {
        String requestPath = "/401.html";
        final String responseBody = fileByPath(requestPath);
        return staticFileMessage(HTML, responseBody);
    }
}
