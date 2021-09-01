package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.SupportedContentType;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.ResponseBody;
import nextstep.jwp.http.response.ResponseHeader;
import nextstep.jwp.http.response.ResponseLine;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) throws Exception {

        HttpResponse httpResponse = create404Response(request);
        if (request.isPost()) {
            httpResponse = doPost(request);
        }
        if (request.isGet()) {
            httpResponse = doGet(request);
        }

        return httpResponse;
    }

    protected HttpResponse doPost(HttpRequest request) throws Exception {
        return create404Response(request);
    }

    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return create404Response(request);
    }


    protected HttpResponse replyAfterLogin302Response(String responseBody, String location) {
        return new HttpResponse(
                new ResponseLine("302", "Found"),
                new ResponseHeader.ResponseHeaderBuilder(SupportedContentType.HTML,
                        responseBody.getBytes().length).
                        location(location).build(),
                ResponseBody.createByString(responseBody));
    }

    protected HttpResponse replyOkResponse(String responseBody) {
        return new HttpResponse(
                new ResponseLine("200", "OK"),
                new ResponseHeader.ResponseHeaderBuilder(SupportedContentType.HTML,
                        responseBody.getBytes().length).build(),
                ResponseBody.createByString(responseBody));
    }

    protected HttpResponse replyOkCssResponse(String responseBody) {
        return new HttpResponse(
                new ResponseLine("200", "OK"),
                new ResponseHeader.ResponseHeaderBuilder(SupportedContentType.CSS,
                        responseBody.getBytes().length).build(),
                ResponseBody.createByString(responseBody));
    }

    protected HttpResponse replyOkJsResponse(String responseBody) {
        return new HttpResponse(
                new ResponseLine("200", "OK"),
                new ResponseHeader.ResponseHeaderBuilder(SupportedContentType.JS,
                        responseBody.getBytes().length).build(),
                ResponseBody.createByString(responseBody));
    }

    protected HttpResponse create404Response(HttpRequest httpRequest) throws IOException {
        String responseBody = ResponseBody.createByPath("/404.html").getResponseBody();
        return new HttpResponse(
                new ResponseLine("404", "Not Found"),
                new ResponseHeader.ResponseHeaderBuilder(SupportedContentType.HTML,
                        responseBody.getBytes().length).build(),
                ResponseBody.createByString(responseBody));
    }
}

