package nextstep.jwp.framework.infrastructure.random;

import java.util.UUID;

public class UUIdGenerator implements RandomIdGenerator {

    @Override
    public String generateId() {
        return UUID.randomUUID().toString();
    }
}
