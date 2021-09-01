package nextstep.jwp.domain;

import java.util.UUID;

public class UUIDStrategy implements TokenStrategy {

    @Override
    public String token() {
        return UUID.randomUUID().toString();
    }
}
