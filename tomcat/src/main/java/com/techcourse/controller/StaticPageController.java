package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import org.apache.coyote.http11.FileReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpResponseHeaders;
import org.apache.coyote.http11.response.HttpStatusCode;

public class StaticPageController {

    public static HttpResponse getStaticPage(HttpRequest httpRequest) throws URISyntaxException, IOException {
        FileReader fileReader = FileReader.getInstance();
        HttpStatusCode statusCode = HttpStatusCode.OK;
        HttpResponseBody httpResponseBody = new HttpResponseBody(
            fileReader.readFile(httpRequest.getHttpRequestPath()));

        HttpResponseHeaders httpResponseHeaders = new HttpResponseHeaders(new HashMap<>());
        httpResponseHeaders.setContentType(httpRequest);
        httpResponseHeaders.setContentLength(httpResponseBody);
        return new HttpResponse(statusCode, httpResponseHeaders, httpResponseBody);
    }
}
