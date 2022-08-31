package org.apache.coyote.web;

import static org.apache.coyote.support.HttpHeader.CONTENT_TYPE;

import nextstep.jwp.controller.UserLoginController;
import nextstep.jwp.controller.dto.UserLoginRequest;
import org.apache.coyote.BodyResponse;
import org.apache.coyote.NoBodyResponse;
import org.apache.coyote.Request;
import org.apache.coyote.Response;
import org.apache.coyote.support.ContentType;
import org.apache.coyote.support.HttpHeader;
import org.apache.coyote.support.HttpHeaderFactory;
import org.apache.coyote.support.HttpHeaderFactory.Pair;
import org.apache.coyote.support.HttpHeaders;
import org.apache.coyote.support.HttpStatus;

public class RequestHandler {

    public Response handle(final Request request) {

        if (request.isSameRequestUrl("/login")) {
            UserLoginRequest userLoginRequest = UserLoginRequest.from(request.getQueryParameters());
            ResponseEntity<String> response = new UserLoginController().doGet(userLoginRequest);

            HttpHeaders httpHeaders = HttpHeaderFactory.create(
                    new Pair(CONTENT_TYPE, ContentType.TEXT_HTML_CHARSET_UTF_8.getValue()),
                    new Pair(HttpHeader.LOCATION, "/index.html")
            );

            return new NoBodyResponse(response.getHttpStatus(), httpHeaders);
        }

        HttpHeaders httpHeaders = HttpHeaderFactory.create(
                new Pair(CONTENT_TYPE, ContentType.TEXT_HTML_CHARSET_UTF_8.getValue())
        );
        return new BodyResponse(HttpStatus.OK, httpHeaders, "Hello world!");
    }

}
