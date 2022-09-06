package org.apache.coyote.handler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.domain.FilePath;
import org.apache.coyote.domain.HttpMethod;
import org.apache.coyote.domain.HttpRequest;
import org.apache.coyote.domain.HttpStatusCode;
import org.apache.coyote.domain.MyHttpResponse;
import org.apache.coyote.domain.RedirectUrl;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    public MyHttpResponse run(HttpRequest httpRequest) throws URISyntaxException, IOException {
        final FilePath filePath = FilePath.from(httpRequest.getUri());
        if (httpRequest.getHttpCookie().getJSESSIONID()) {
            return MyHttpResponse.from(filePath, HttpStatusCode.FOUND)
                    .addRedirectUrlHeader(RedirectUrl.from("/index.html"));
        }
        if (httpRequest.getHttpMethod().equals(HttpMethod.GET) && httpRequest.getQueryParam().isEmpty()) {
            return MyHttpResponse.from(filePath, HttpStatusCode.OK);
        }
        return login(httpRequest);
    }

    private static MyHttpResponse login(HttpRequest httpRequest) throws URISyntaxException, IOException {
        final FilePath filePath = FilePath.from(httpRequest.getUri());
        Optional<User> user = InMemoryUserRepository.findByAccount(
                httpRequest.getRequestBody().getRequestBody().get("account"));
        if (user.isPresent()) {
            log.info(user.get().toString());
            if (user.get().checkPassword(httpRequest.getRequestBody().getRequestBody().get("password"))) {
                return MyHttpResponse.from(filePath, HttpStatusCode.FOUND)
                        .addRedirectUrlHeader(RedirectUrl.from("/index.html"))
                        .addSetCookieHeader(httpRequest.getHttpCookie());
            }
        }
        return MyHttpResponse.from(filePath, HttpStatusCode.FOUND)
                .addRedirectUrlHeader(RedirectUrl.from("/401.html"));
    }
}
