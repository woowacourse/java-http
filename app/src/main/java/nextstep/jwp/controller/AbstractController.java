package nextstep.jwp.controller;

import nextstep.jwp.http.HttpHeader;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.ResponseStatus;
import nextstep.jwp.util.FileUtil;

public abstract class AbstractController implements Controller{

    @Override
    public boolean isMatchingController(HttpRequest httpRequest) {
        return isMatchingHttpMethod(httpRequest) && isMatchingUriPath(httpRequest);
    }

    protected HttpResponse renderPage(String uriPath) {
        try {
            String responseBody = FileUtil.readFileByUriPath(uriPath);
            return HttpResponse.status(ResponseStatus.OK,
                HttpHeader.getHTMLResponseHeader(responseBody),
                responseBody);
        } catch (IllegalArgumentException e) {
            // TODO: 에러 파일 출력
            return null;
        }
    }

    abstract boolean isMatchingHttpMethod(HttpRequest httpRequest);

    abstract boolean isMatchingUriPath(HttpRequest httpRequest);
}
