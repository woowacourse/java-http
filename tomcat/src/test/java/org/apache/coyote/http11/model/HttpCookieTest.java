package org.apache.coyote.http11.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    void create() {
        UUID uuid = UUID.randomUUID();
        System.out.println(uuid.toString());
    }

}