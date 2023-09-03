package org.apache.coyote.http.controller;

import org.apache.coyote.http.*;
import org.apache.coyote.util.FileUtil;

public class ViewRenderer {

    public void render(HttpRequest httpRequest, HttpResponseComposer httpResponseComposer) {
        String viewName = httpResponseComposer.getViewName();
        if (viewName == null) {
            viewName = httpRequest.getPath();
        }

        String content = FileUtil.readStaticFile(viewName);
        ContentType contentType = ContentType.from(viewName.substring(viewName.lastIndexOf(".") + 1));

        HttpResponse httpResponse = new HttpResponse.Builder()
                .statusCode(StatusCode.OK)
                .httpMessage(HttpBody.of(content, contentType))
                .build();
        httpResponseComposer.setHttpResponse(httpResponse);
    }
}
