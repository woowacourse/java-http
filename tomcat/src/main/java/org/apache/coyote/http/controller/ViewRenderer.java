package org.apache.coyote.http.controller;

import org.apache.coyote.http.*;
import org.apache.coyote.util.FileUtil;

public class ViewRenderer {

    public void render(HttpRequest httpRequest, HttpResponse httpResponse) {
        String filePath = httpRequest.getPath();
        if (httpResponse.getForwardPath() != null) {
            filePath = httpResponse.getForwardPath();
        }

        String content = FileUtil.readStaticFile(filePath);
        ContentType contentType = ContentType.fromFilePath(filePath);

        httpResponse.setStatusCode(StatusCode.OK);
        httpResponse.setBody(content);
        httpResponse.setContentType(contentType.value);
    }
}
