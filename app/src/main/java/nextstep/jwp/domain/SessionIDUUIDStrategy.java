package nextstep.jwp.domain;

import java.util.UUID;

public class SessionIDUUIDStrategy implements SessionIDStrategy {

    @Override
    public String token() {
        return UUID.randomUUID().toString();
    }
}
