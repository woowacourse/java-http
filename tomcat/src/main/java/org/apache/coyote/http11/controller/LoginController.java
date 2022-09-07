package org.apache.coyote.http11.controller;

import java.io.IOException;
import java.util.UUID;
import org.apache.coyote.http11.ControllerAdvice;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public HttpResponse getResponse(HttpRequest httpRequest) throws IOException {
        if (HttpMethod.GET == httpRequest.getHttpMethod()) {
            HttpCookie httpCookie = httpRequest.getCookie();
            if(httpCookie.isExistJSESSIONID()) {
                return HttpResponse.createWithoutBody(HttpStatus.FOUND, "/index");
            }
            return HttpResponse.createWithBody(HttpStatus.OK, httpRequest.getRequestLine());
        }

        if (HttpMethod.POST == httpRequest.getHttpMethod()) {
            log.info("로그인 성공! 아이디: " + httpRequest.getBodyValue("account"));
            final UUID uuid = UUID.randomUUID();
            return HttpResponse.createWithoutBodyForJSession(HttpStatus.FOUND, "/index", uuid.toString());
        }
        return ControllerAdvice.handleNotFound();
    }
}
