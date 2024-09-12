package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Set;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("요청 문자열을 올바르게 변환한다.")
    @Test
    void parseHttpRequest() throws IOException {
        Session session = new Session("1234");
        new SessionManager().add(session);
        String rawRequest = String.join("\r\n",
                "POST /hello?me=potato&you=gamja HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: 4",
                "Cookie: JSESSIONID=1234",
                "",
                "body");
        BufferedReader bufferedReader = new BufferedReader(new StringReader(rawRequest));
        HttpRequest request = new HttpRequestReader(bufferedReader).read();

        assertAll(
                () -> assertThat(request.getPath()).isEqualTo("/hello"),
                () -> assertThat(request.getMethod()).isEqualTo(HttpMethod.POST),
                () -> assertThat(request.getSessionId()).isEqualTo("1234"),
                () -> assertThat(request.getBody()).isEqualTo("body"),
                () -> assertThat(request.getQuery("me")).isEqualTo("potato"),
                () -> assertThat(request.getQuery("you")).isEqualTo("gamja"),
                () -> assertThat(request.getQueryKeys()).isEqualTo(Set.of("me", "you"))
        );
    }

}
