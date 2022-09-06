package org.apache.coyote.http11.url;

import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.utils.IOUtils;
import org.apache.coyote.http11.utils.UrlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Login extends Url {
    private static final Logger log = LoggerFactory.getLogger(Login.class);

    public Login(final String url, HttpMethod httpMethod) {
        super(url, httpMethod);
    }

    @Override
    public Http11Response getResponse(HttpHeaders httpHeaders) {
        return new Http11Response(getPath(), HttpStatus.OK, IOUtils.readResourceFile(getPath()));
    }

    @Override
    public Http11Response postResponse(HttpHeaders httpHeaders, String requestBody) {
        HttpRequest request = UrlParser.extractRequest(requestBody);
        User user = InMemoryUserRepository.findByAccount(request.get("account"))
                .orElse(null);
        if (Objects.isNull(user)) {
            return new Http11Response(getPath(), HttpStatus.UNAUTHORIZED, IOUtils.readResourceFile("/401.html"));
        }
        if (user.checkPassword(request.get("password"))) {
            return new Http11Response(getPath(), HttpStatus.FOUND, IOUtils.readResourceFile("/index.html"));
        }
        log.info("user : {}", user);
        return new Http11Response(getPath(), HttpStatus.UNAUTHORIZED, IOUtils.readResourceFile("/401.html"));
    }
}
