package org.apache.coyote.requestmapping;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.Documented;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegistryTest {

    @Test
    @DisplayName("매핑 정보를 통해 알맞은 요청을 찾는다.")
    void findMapping() {
        Registry.register();
        HttpRequest httpRequest = new HttpRequest("GET /index.html HTTP/1.1");

        final HttpResponse mapping = Registry.findMapping(httpRequest);


    }
}
