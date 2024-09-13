package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.message.request.HttpMethod;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.HttpUrl;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.junit.jupiter.api.Test;

class ViewHttpHandlerTest {

    @Test
    void handleTest() throws IOException {
        // given
        ViewHttpHandler viewHttpHandler = new ViewHttpHandler("login");
        final URL resource = getClass().getClassLoader().getResource("static/login.html");

        // when
        HttpResponse response = viewHttpHandler.handle(new HttpRequest(HttpMethod.GET, new HttpUrl("/login")));
        byte[] expected = Files.readAllBytes(new File(resource.getFile()).toPath());

        // then
        assertThat(response.getBody())
                .isEqualTo(expected);
    }
}
