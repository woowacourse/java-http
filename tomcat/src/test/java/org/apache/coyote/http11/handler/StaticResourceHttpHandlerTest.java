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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticResourceHttpHandlerTest {

    @Test
    @DisplayName("정적 리소스 요청을 받으면 해당 리소스를 반환한다.")
    void handleTest() throws IOException {
        // given
        StaticResourceHttpHandler staticResourceHttpHandler = new StaticResourceHttpHandler();
        HttpRequest request = new HttpRequest(HttpMethod.GET, new HttpUrl("/index.html"));
        final URL resource = getClass().getClassLoader().getResource("static/index.html");

        // when
        HttpResponse response = staticResourceHttpHandler.handle(request);
        byte[] expected = Files.readAllBytes(new File(resource.getFile()).toPath());

        // then
        assertThat(response.getBody())
                .isEqualTo(expected);
    }
}
