package org.apache.catalina;

import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(AbstractController.class);

    @Override
    public String service(final Http11Request request, final Http11Response response) {
        //Todo: HTTP 매서드 매핑 방식 수정 필요 [2025-09-05 17:18:12]
        return switch (request.getMethod()) {
            case "GET" -> toGet(request, response);
            case "POST" -> toPost(request, response);
            default -> handlingUnsupportedMethod(request, response);
        };
    }

    public String handlingUnsupportedMethod(final Http11Request request,
                                            final Http11Response response
    ) {
        log.warn("Method:{} Path:{} 지원하지 않는 Method 입니다.", request.getMethod(), request.getRequestTarget());
        response.setState(405);
        return "/4xx.html";
    }
}
