package nextstep.jwp.handler;

import java.io.IOException;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.Response;

public class StaticFileHandler extends AbstractHandler {

    public StaticFileHandler(Request request) {
        super(request);
    }

    @Override
    public Response getMessage() throws IOException {
        final String responseBody = fileByPath(request.path());
        return new Response(staticFileMessage(request.fileType(), responseBody));
    }
}
