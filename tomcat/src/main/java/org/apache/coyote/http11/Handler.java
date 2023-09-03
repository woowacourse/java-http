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
import java.util.Optional;

public class Handler {
    private static final Logger log = LoggerFactory.getLogger(Handler.class);
    private static final String INDEX_HTML = "/index.html";
    private static final String UNAUTHORIZED_HTML = "/401.html";

    public Response handle(final Request request) throws IOException {
        final RequestLine requestLine = request.getRequestLine();

        if (requestLine.getHttpMethod() == HttpMethod.GET) {
            final StaticResource staticResource = StaticResource.from(requestLine.getPath());
            final ResponseBody responseBody = ResponseBody.from(staticResource);

            return Response.of(responseBody, HttpStatus.OK);
        }

        if (requestLine.getHttpMethod() == HttpMethod.POST && requestLine.getPath().startsWith("/login")) {
            final RequestBody requestBody = request.getRequestBody();
            final String account = requestBody.getParamValue("account");
            final String password = requestBody.getParamValue("password");
            final Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);

            if (userOptional.isPresent() && userOptional.get().checkPassword(password)) {
                log.info("user = {}", userOptional.get());
                final StaticResource staticResource = StaticResource.from(INDEX_HTML);
                final ResponseBody responseBody = ResponseBody.from(staticResource);
                return Response.redirect(INDEX_HTML, responseBody, HttpStatus.FOUND);
            }
            final StaticResource staticResource = StaticResource.from(UNAUTHORIZED_HTML);
            final ResponseBody responseBody = ResponseBody.from(staticResource);
            return Response.redirect(UNAUTHORIZED_HTML, responseBody, HttpStatus.FOUND);
        }

        return null;
    }
}
