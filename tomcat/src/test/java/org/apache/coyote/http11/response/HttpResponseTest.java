package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void redirect() {
        HttpResponse response = HttpResponse.defaultResponse();

        response.redirect("HTTP/1.1", "/index.html");

        assertThat(response.getResponseHeader().get("Location")).isEqualTo("/index.html");
    }

}
