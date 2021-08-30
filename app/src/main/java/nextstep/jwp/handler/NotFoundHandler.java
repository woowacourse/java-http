package nextstep.jwp.handler;

import java.io.IOException;
import nextstep.jwp.model.FileType;
import nextstep.jwp.model.PathType;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.Response;

public class NotFoundHandler extends AbstractHandler {

    @Override
    public Response getMessage(Request request) throws IOException {
        final String responseBody = fileByPath(PathType.NOT_FOUND.resource());
        return staticFileMessage(FileType.HTML, responseBody);
    }
}
