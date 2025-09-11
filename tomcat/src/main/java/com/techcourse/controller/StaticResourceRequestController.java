package com.techcourse.controller;

import com.techcourse.controller.core.AbstractRequestController;
import com.techcourse.util.FileUtil;
import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpVersion;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.ResponseBody;

public class StaticResourceRequestController extends AbstractRequestController {
    private final HttpVersion httpVersion;

    public StaticResourceRequestController(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        String fileName = FileUtil.createFileName(httpRequest.getFilePath());

        if ("/static/favicon.ico".equals(fileName)) {
            return HttpResponse.noContent(httpVersion, ContentType.IMAGE_X_ICON, HttpCookie.empty(),
                    ResponseBody.empty());
        }

        if ("/static/.well-known/appspecific/com.chrome.devtools.json".equals(fileName)) {
            return HttpResponse.noContent(httpVersion, ContentType.APPLICATION_JSON, HttpCookie.empty(),
                    ResponseBody.empty());
        }

        return HttpResponse.ok(httpVersion, httpRequest.getContentType(), HttpCookie.empty(),
                ResponseBody.createBy(httpRequest));
    }
}
