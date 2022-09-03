package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginHandler extends ResourceHandler{

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final String path = httpRequest.getPath();
        if (path.contains("?")) {
            // 쿼리 파싱 후 로그인처리
        }
        // login 페이지 반환
        return generateResourceResponse(httpRequest);
    }
}
