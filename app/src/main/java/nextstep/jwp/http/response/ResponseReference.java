package nextstep.jwp.http.response;

import java.io.IOException;
import nextstep.jwp.http.SupportedContentType;
import nextstep.jwp.http.auth.HttpSession;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.ResponseHeader.ResponseHeaderBuilder;

public class ResponseReference {
    public static HttpResponse createRedirectResponse(HttpRequest request, String location) throws IOException {
        String responseBody = ResponseBody.createStaticFileByFileName(location).getResponseBody();
        ResponseStatus responseStatus = ResponseStatus.FOUND;
        return new HttpResponse(
                new ResponseLine(responseStatus.getStatusCode(), responseStatus.getStatusMessage()),
                new ResponseHeader.ResponseHeaderBuilder(SupportedContentType.HTML,
                        responseBody.getBytes().length).
                        location(location)
                        .build(),
                ResponseBody.createByString(responseBody));
    }

    public static HttpResponse createSessionRedirectResponse(HttpRequest request, HttpSession session, String location)
            throws IOException {
        String responseBody = ResponseBody.createStaticFileByFileName(location).getResponseBody();
        ResponseStatus responseStatus = ResponseStatus.FOUND;

        ResponseLine responseLine = new ResponseLine(responseStatus.getStatusCode(), responseStatus.getStatusMessage());
        ResponseHeader responseHeader = new ResponseHeaderBuilder(SupportedContentType.HTML,
                responseBody.getBytes().length).
                location(location)
                .build();
        if (request.getCookie() == null) {
            responseHeader = new ResponseHeaderBuilder(SupportedContentType.HTML,
                    responseBody.getBytes().length).
                    location(location)
                    .cookie(session.getId())
                    .build();
        }

        ResponseBody byString = ResponseBody.createByString(responseBody);
        return new HttpResponse(responseLine, responseHeader, byString);


    }

    public static HttpResponse create200Response(HttpRequest request) throws IOException {
        String responseBody = ResponseBody.createStaticFileByFileName(request.getUri() + ".html").getResponseBody();
        ResponseStatus responseStatus = ResponseStatus.OK;
        return new HttpResponse(
                new ResponseLine(responseStatus.getStatusCode(), responseStatus.getStatusMessage()),
                new ResponseHeader.ResponseHeaderBuilder(SupportedContentType.HTML,
                        responseBody.getBytes().length)
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
