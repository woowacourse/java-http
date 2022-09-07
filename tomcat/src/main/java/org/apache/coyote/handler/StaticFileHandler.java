package org.apache.coyote.handler;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.domain.FilePath;
import org.apache.coyote.domain.request.HttpRequest;
import org.apache.coyote.domain.response.HttpStatusCode;
import org.apache.coyote.domain.response.MyHttpResponse;

public class StaticFileHandler implements Handler {
    @Override
    public MyHttpResponse run(HttpRequest httpRequest) throws URISyntaxException, IOException {
        final FilePath filePath = FilePath.from(httpRequest.getRequestLine().getPath().getPath());
        return MyHttpResponse.from(filePath, HttpStatusCode.OK);
    }
}
