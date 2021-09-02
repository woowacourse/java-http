package nextstep.common;

import nextstep.jwp.framework.infrastructure.random.RandomIdGenerator;

public class MockIdGenerator implements RandomIdGenerator {

    @Override
    public String generateId() {
        return "1a2b3c";
    }
}
