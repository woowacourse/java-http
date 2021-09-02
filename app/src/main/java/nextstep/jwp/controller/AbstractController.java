package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.SupportedContentType;
import nextstep.jwp.http.auth.HttpSession;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.ResponseBody;
import nextstep.jwp.http.response.ResponseHeader;
import nextstep.jwp.http.response.ResponseLine;
import nextstep.jwp.http.response.ResponseStatus;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) throws Exception {

        HttpResponse httpResponse = create404Response();
        if (request.isPost()) {
            httpResponse = doPost(request);
        }
        if (request.isGet()) {
            httpResponse = doGet(request);
        }

        return httpResponse;
    }

    protected HttpResponse doPost(HttpRequest request) throws Exception {
        return create404Response();
    }

    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return create404Response();
    }


    protected HttpResponse createRedirectResponse(HttpRequest request, String location) throws IOException {
        String responseBody = ResponseBody.createStaticFileByFileName(location).getResponseBody();
        ResponseStatus responseStatus = ResponseStatus.FOUND;
        return new HttpResponse(
                new ResponseLine(responseStatus.getStatusCode(), responseStatus.getStatusMessage()),
                new ResponseHeader.ResponseHeaderBuilder(SupportedContentType.HTML,
                        responseBody.getBytes().length).
                        location(location)
                        .cookie(request.getCookie())
                        .build(),
                ResponseBody.createByString(responseBody));
    }

    protected HttpResponse createSessionRedirectResponse(HttpSession session, String location) throws IOException {
        String responseBody = ResponseBody.createStaticFileByFileName(location).getResponseBody();
        ResponseStatus responseStatus = ResponseStatus.FOUND;
        return new HttpResponse(
                new ResponseLine(responseStatus.getStatusCode(), responseStatus.getStatusMessage()),
                new ResponseHeader.ResponseHeaderBuilder(SupportedContentType.HTML,
                        responseBody.getBytes().length).
                        location(location)
                        .cookie(session.getId())
                        .build(),
                ResponseBody.createByString(responseBody));
    }

    protected HttpResponse create200Response(HttpRequest request) throws IOException {
        String responseBody = ResponseBody.createStaticFileByFileName(request.getUri() + ".html").getResponseBody();
        ResponseStatus responseStatus = ResponseStatus.OK;
        return new HttpResponse(
                new ResponseLine(responseStatus.getStatusCode(), responseStatus.getStatusMessage()),
                new ResponseHeader.ResponseHeaderBuilder(SupportedContentType.HTML,
                        responseBody.getBytes().length)
                        .cookie(request.getCookie())
                        .build(),
                ResponseBody.createByString(responseBody));
    }

    protected HttpResponse create404Response() throws IOException {
        String responseBody = ResponseBody.createStaticFileByFileName("/404.html").getResponseBody();
        ResponseStatus responseStatus = ResponseStatus.NOT_FOUND;
        return new HttpResponse(
                new ResponseLine(responseStatus.getStatusCode(), responseStatus.getStatusMessage()),
                new ResponseHeader.ResponseHeaderBuilder(SupportedContentType.HTML,
                        responseBody.getBytes().length).build(),
                ResponseBody.createByString(responseBody));
    }
}

