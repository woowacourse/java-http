package org.apache.coyote.http11.component.common;

import java.util.List;
import java.util.Objects;

import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;

public class FormUrlEncodedBody extends Body {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    public FormUrlEncodedBody(final String plaintext) {
        super(plaintext);
        logUser();
    }

    protected void consume(final String plaintext) {
        if (Objects.isNull(plaintext)) {
            return;
        }
        final var params = List.of(plaintext.split(PARAMETER_DELIMITER));
        params.forEach(this::convertParam);
    }

    private void convertParam(final String param) {
        Objects.requireNonNull(param);
        final var pair = List.of(param.split(KEY_VALUE_DELIMITER));
        if (pair.size() != 2) {
            throw new IllegalArgumentException("올바르지 않은 파라미터 갯수");
        }
        super.add(pair.get(0), pair.get(1));
    }

    private void logUser() {
        final var username = get("account");
        final var password = get("password");

        if (Objects.isNull(username) || Objects.isNull(password)) {
            return;
        }

        final var user = InMemoryUserRepository.findByAccount(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 아이디"));

        if (user.checkPassword(password)) {
            log.info("{}", user);
        }
    }
}
