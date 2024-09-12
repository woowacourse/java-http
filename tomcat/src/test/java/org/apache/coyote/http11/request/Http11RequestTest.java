package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class Http11RequestTest {

    @Test
    @DisplayName("요청에 따라 정적 파일 요청인지 아닌지 알 수 있다.")
    void isStaticRequest_static() {
        String staticRequest = """
                GET /index.html HTTP/1.1
                Host: localhost:8080
                Connection: keep-alive
                
                
                """;
        InputStream inputStream = new ByteArrayInputStream(staticRequest.getBytes());
        Http11Request request;
        try {
            request = Http11Request.from(inputStream);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        assertThat(request.isStaticRequest()).isTrue();
    }

    @Test
    @DisplayName("요청에 따라 정적 파일 요청인지 아닌지 알 수 있다.")
    void isStaticRequest_nonStatic() {
        String staticRequest = """
                POST /login HTTP/1.1
                Host: localhost:8080
                Connection: keep-alive
                
                
                """;
        InputStream inputStream = new ByteArrayInputStream(staticRequest.getBytes());
        try {
            Http11Request from = Http11Request.from(inputStream);
            assertThat(from.isStaticRequest()).isFalse();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
