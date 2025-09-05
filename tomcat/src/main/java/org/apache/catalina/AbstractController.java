package org.apache.catalina;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    @Override
    public void service(Http11Request request, Http11Response response) {
        switch (request.method()) {
            case "GET" -> toGet(request, response);
            case "POST" -> toPost(request, response);
            default -> handlingUnsupportedMethod(request, response);
        }
    }

    public void handlingUnsupportedMethod(Http11Request request, Http11Response response) {
        log.warn("Method:{} Path:{} 지원하지 않는 Method 입니다.", request.method(), request.path());
        response.setStatusCode(405);
        response.setResourcePath("/405.html");
    }
}
