package org.apache.coyote.http11.url;

import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.dto.LoginQueryDataDto;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.utils.UrlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Login extends Url {
    private static final Logger log = LoggerFactory.getLogger(Login.class);

    public Login(final String url) {
        super(url);
    }

    @Override
    public Http11Response getResponse(String httpMethod) {
        LoginQueryDataDto queryData = UrlParser.loginQuery(getPath());

        if (queryData == null) {
            return new Http11Response(ContentType.from(getPath()), HttpStatus.OK, "login.html");
        }

        User user = InMemoryUserRepository.findByAccount(queryData.getAccount())
                .orElse(null);
        if (Objects.isNull(user)) {
            return new Http11Response(ContentType.from(getPath()), HttpStatus.UNAUTHORIZED, "401.html");
        }
        if (user.checkPassword(queryData.getPassword())) {
            return new Http11Response(ContentType.from(getPath()), HttpStatus.FOUND, "index.html");
        }
        log.info("user : {}", user);

        return new Http11Response(ContentType.from(getPath()), HttpStatus.UNAUTHORIZED, "401.html");
    }
}
