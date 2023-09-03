package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StaticResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Handler {
    private static final Logger log = LoggerFactory.getLogger(Handler.class);

    public Response handle(final Request request) throws IOException {
        final RequestLine requestLine = request.getRequestLine();
        if (requestLine.getHttpMethod() == HttpMethod.GET) {
            final StaticResource staticResource = StaticResource.from(requestLine.getPath());
            final ResponseBody responseBody = ResponseBody.from(staticResource);

            return Response.of(responseBody, HttpStatus.OK);
        }

        if (requestLine.getHttpMethod() == HttpMethod.POST && requestLine.getPath().startsWith("/login")) {
            final RequestBody requestBody = request.getRequestBody();
            final User user = InMemoryUserRepository.findByAccount(requestBody.getParamValue("account")).orElseThrow();
            log.info("user = {}", user);
            final StaticResource staticResource = StaticResource.from(requestLine.getPath());
            final ResponseBody responseBody = ResponseBody.from(staticResource);
            return Response.of(responseBody, HttpStatus.OK);
        }

        return null;
    }
}
