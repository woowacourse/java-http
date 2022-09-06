package org.apache.coyote.http11.url;

import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.dto.LoginQueryDataDto;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpMethod;
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
        LoginQueryDataDto queryData = UrlParser.loginQuery(requestBody);
        User user = InMemoryUserRepository.findByAccount(queryData.getAccount())
                .orElse(null);
        if (Objects.isNull(user)) {
            return new Http11Response(getPath(), HttpStatus.UNAUTHORIZED, IOUtils.readResourceFile("/401.html"));
        }
        if (user.checkPassword(queryData.getPassword())) {
            return new Http11Response(getPath(), HttpStatus.OK, IOUtils.readResourceFile("/index.html"));
        }
        log.info("user : {}", user);
        return new Http11Response(getPath(), HttpStatus.UNAUTHORIZED, IOUtils.readResourceFile("/401.html"));
    }
}
