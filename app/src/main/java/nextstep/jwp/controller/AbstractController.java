package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.http.HttpHeader;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.ResponseStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.util.FileUtil;

public abstract class AbstractController implements Controller {

    @Override
    public boolean isMatchingController(HttpRequest httpRequest) {
        return isMatchingHttpMethod(httpRequest) && isMatchingUriPath(httpRequest);
    }

    protected HttpResponse renderPage(String uriPath) {
        try {
            String responseBody = FileUtil.readHTMLFileByUriPath(uriPath);
            return HttpResponse.status(ResponseStatus.OK,
                HttpHeader.getHTMLResponseHeader(responseBody),
                responseBody);
        } catch (IllegalArgumentException e) {
            // TODO: 에러 파일 출력
            return null;
        }
    }

    protected HttpResponse applyStaticFile(String uriPath) {
        try {
            String responseBody = FileUtil.readStaticFileByUriPath(uriPath);
            return HttpResponse.status(ResponseStatus.OK,
                HttpHeader.getHTMLResponseHeader(responseBody),
                responseBody);
        } catch (IllegalArgumentException e) {
            // TODO: 에러 파일 출력
            return null;
        }
    }

    protected HttpResponse applyCSSFile(String uriPath) {
        try {
            String responseBody = FileUtil.readStaticFileByUriPath(uriPath);
            return HttpResponse.status(ResponseStatus.OK,
                HttpHeader.getCSSResponseHeader(responseBody),
                responseBody);
        } catch (IllegalArgumentException e) {
            // TODO: 에러 파일 출력
            return null;
        }
    }

    protected HttpResponse redirect(String redirectUrl) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Location", redirectUrl);

        return HttpResponse.status(ResponseStatus.FOUND, new HttpHeader(headers));
    }

    abstract boolean isMatchingHttpMethod(HttpRequest httpRequest);

    abstract boolean isMatchingUriPath(HttpRequest httpRequest);
}
