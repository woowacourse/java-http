package nextstep.jwp.http.response;

import java.io.IOException;
import nextstep.jwp.http.SupportedContentType;
import nextstep.jwp.http.auth.HttpSession;
import nextstep.jwp.http.request.HttpRequest;

public class ResponseReference {
    public static HttpResponse createRedirectResponse(HttpRequest request, String location) throws IOException {
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

    public static HttpResponse createSessionRedirectResponse(HttpSession session, String location) throws IOException {
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

    public static HttpResponse create200Response(HttpRequest request) throws IOException {
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

    public static HttpResponse create404Response() throws IOException {
        String responseBody = ResponseBody.createStaticFileByFileName("/404.html").getResponseBody();
        ResponseStatus responseStatus = ResponseStatus.NOT_FOUND;
        return new HttpResponse(
                new ResponseLine(responseStatus.getStatusCode(), responseStatus.getStatusMessage()),
                new ResponseHeader.ResponseHeaderBuilder(SupportedContentType.HTML,
                        responseBody.getBytes().length).build(),
                ResponseBody.createByString(responseBody));
    }
}
