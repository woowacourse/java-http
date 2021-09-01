package nextstep.jwp.mapping;

import java.io.IOException;
import nextstep.jwp.FileAccess;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.content.ContentType;
import nextstep.jwp.http.response.status.HttpStatus;

public class FileAccessHandler implements Handler {

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {

        String resource = new FileAccess(request.getPath()).getFile();

        ContentType contentType = getContentType(request.getPath());

        response.setStatusLine(request.getProtocolVersion(), HttpStatus.OK);
        response.addResponseHeader("Content-Type", contentType.getType());
        response.addResponseHeader("Content-Length", String.valueOf(resource.getBytes().length));
        response.setResponseBody(resource);
    }

    private ContentType getContentType(String path) {
        String extension = path.substring(path.indexOf("."));

        return ContentType.findContentType(extension);
    }
}
