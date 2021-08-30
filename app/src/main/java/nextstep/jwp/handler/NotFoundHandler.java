package nextstep.jwp.handler;

import java.io.IOException;
import nextstep.jwp.model.FileType;
import nextstep.jwp.model.PathType;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.Response;

public class NotFoundHandler extends AbstractHandler {

    public NotFoundHandler(Request request) {
        super(request);
    }

    @Override
    public Response getMessage() throws IOException {
        final String responseBody = fileByPath(PathType.NOT_FOUND.resource());
        return new Response(staticFileMessage(FileType.HTML, responseBody));
    }
}
