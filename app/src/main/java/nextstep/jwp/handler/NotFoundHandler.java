package nextstep.jwp.handler;

import java.io.IOException;
import nextstep.jwp.model.Request;

public class NotFoundHandler extends AbstractHandler {

    @Override
    public String message(Request request) throws IOException {
        final String responseBody = fileByPath(FILE_401_HTML);
        return staticFileMessage(HTML, responseBody);
    }
}
