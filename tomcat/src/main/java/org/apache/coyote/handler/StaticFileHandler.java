package org.apache.coyote.handler;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.domain.FilePath;
import org.apache.coyote.domain.HttpRequest;
import org.apache.coyote.domain.HttpStatusCode;
import org.apache.coyote.domain.MyHttpResponse;

public class StaticFileHandler implements Handler {
    @Override
    public MyHttpResponse run(HttpRequest httpRequest) throws URISyntaxException, IOException {
        final FilePath filePath = FilePath.from(httpRequest.getUri());
        return MyHttpResponse.from(filePath, HttpStatusCode.OK);
    }
}
