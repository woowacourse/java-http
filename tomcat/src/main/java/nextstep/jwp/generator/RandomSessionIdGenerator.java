package nextstep.jwp.generator;

import java.util.UUID;

public class RandomSessionIdGenerator implements SessionIdGenerator {
    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
