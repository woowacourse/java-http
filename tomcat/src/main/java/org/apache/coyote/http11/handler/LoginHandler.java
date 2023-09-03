package org.apache.coyote.http11.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpHeaderName;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.QueryParams;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public HttpResponse handle(final HttpRequest request) throws IOException {
        if (!request.hasParams("account", "password")) {
            URL resource = getClass().getClassLoader().getResource("static/login.html");
            Path path = new File(resource.getPath()).toPath();
            String content = new String(Files.readAllBytes(path));
            HttpHeaders headers = new HttpHeaders();
            headers.addHeader(HttpHeaderName.CONTENT_TYPE, ContentType.TEXT_HTML.getDetail());
            headers.addHeader(HttpHeaderName.CONTENT_LENGTH, String.valueOf(content.getBytes().length));
            return HttpResponse.create(StatusCode.OK, headers, content);
        }

        if (loginSuccess(request)) {
            HttpHeaders headers = new HttpHeaders();
            headers.addHeader(HttpHeaderName.LOCATION, "/index.html");
            return HttpResponse.create(StatusCode.FOUND, headers);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.addHeader(HttpHeaderName.LOCATION, "/401.html");
        return HttpResponse.create(StatusCode.FOUND, headers);
    }

    private boolean loginSuccess(final HttpRequest request) {
        QueryParams params = request.getParams();
        String account = params.getParam("account");
        String password = params.getParam("password");

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .isPresent();
    }
}
