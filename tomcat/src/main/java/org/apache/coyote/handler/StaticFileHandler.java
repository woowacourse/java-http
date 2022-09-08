package org.apache.coyote.handler;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.domain.FilePath;
import org.apache.coyote.domain.request.HttpRequest;
import org.apache.coyote.domain.response.HttpResponse;
import org.apache.coyote.domain.response.HttpStatusCode;

public class StaticFileHandler implements Handler {
    @Override
    public HttpResponse run(HttpRequest httpRequest) throws URISyntaxException, IOException {
        final FilePath filePath = FilePath.from(httpRequest.getRequestLine().getPath().getPath());
        return HttpResponse.from(httpRequest.getRequestLine().getHttpVersion(), filePath, HttpStatusCode.OK);
    }
}
