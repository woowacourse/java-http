package org.apache.catalina;

import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(AbstractController.class);

    @Override
    public void service(final Http11Request request, final Http11Response response) {
        //Todo: HTTP 매서드 매핑 방식 수정 필요 [2025-09-05 17:18:12]
        switch (request.method()) {
            case "GET" -> toGet(request, response);
            case "POST" -> toPost(request, response);
            default -> handlingUnsupportedMethod(request, response);
        }
    }

    public void handlingUnsupportedMethod(final Http11Request request,
                                          final Http11Response response
    ) {
        log.warn("Method:{} Path:{} 지원하지 않는 Method 입니다.", request.method(), request.path());
        response.setStatusCode(405);
        response.setResourcePath("/4xx.html");
    }
}
