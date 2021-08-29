package nextstep.jwp.handler;

import java.io.IOException;
import nextstep.jwp.model.FileType;
import nextstep.jwp.model.PathType;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.Response;

public class UnauthorizedHandler extends AbstractHandler {

    public UnauthorizedHandler(Request request) {
        super(request);
    }

    @Override
    public Response getMessage() throws IOException {
        final String responseBody = fileByPath(PathType.UNAUTHORIZED.value() + FileType.HTML.extension());
        return new Response(staticFileMessage(FileType.HTML, responseBody));
    }
}
