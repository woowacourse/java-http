package nextstep.jwp.controller;

import java.util.UUID;

public class SessionIdGenerator implements IdGenerator{

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
