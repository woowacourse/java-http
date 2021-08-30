package nextstep.jwp.handler;

import java.io.IOException;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.Response;

public class StaticFileHandler extends AbstractHandler {

    @Override
    public Response getMessage(Request request) throws IOException {
        final String responseBody = fileByPath(request.path());
        return new Response(staticFileMessage(request.fileType(), responseBody));
    }
}
