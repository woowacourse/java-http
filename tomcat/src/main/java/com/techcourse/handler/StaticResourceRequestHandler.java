package com.techcourse.handler;

import com.techcourse.http.ContentType;
import com.techcourse.http.HttpCookie;
import com.techcourse.http.HttpVersion;
import com.techcourse.http.request.HttpRequest;
import com.techcourse.http.response.HttpResponse;
import com.techcourse.http.response.ResponseBody;
import com.techcourse.util.FileUtil;

public class StaticResourceRequestHandler extends AbstractRequestHandler {
    private final HttpVersion httpVersion;

    public StaticResourceRequestHandler(final HttpVersion httpVersion) {
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
