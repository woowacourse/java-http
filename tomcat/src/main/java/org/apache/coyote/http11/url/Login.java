package org.apache.coyote.http11.url;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.dto.Http11Request;
import org.apache.coyote.http11.utils.StringParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Login extends Url {
    private static final Logger log = LoggerFactory.getLogger(Login.class);

    public Login(final String url) {
        super(url);
    }

    @Override
    public Http11Request getRequest() {
        Http11Request http11Request = StringParser.loginQuery(getPath());

        User user = InMemoryUserRepository.findByAccount(http11Request.getAccount())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호를 잘못 입력하였습니다."));
        log.info("user : {}", user);

        return http11Request;
    }
}
