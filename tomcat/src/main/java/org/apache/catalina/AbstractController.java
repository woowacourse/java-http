package org.apache.catalina;

import org.apache.catalina.exception.MethodNotAllowedException;
import org.apache.coyote.http11.domain.HttpMethod;
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
            case HttpMethod.GET -> toGet(request, response);
            case HttpMethod.POST -> toPost(request, response);
            default -> handlingUnsupportedMethod(request, response);
        };
    }

    public String handlingUnsupportedMethod(final Http11Request request,
                                            final Http11Response response
    ) {
        log.warn("Method:{} Path:{} Method Not Allowed.", request.getMethod(), request.getRequestTarget());
        throw new MethodNotAllowedException(response);
    }
}
