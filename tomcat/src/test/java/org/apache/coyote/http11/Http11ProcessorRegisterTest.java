package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorRegisterTest {

    @Test
    void POST_방식으로_회원가입한다() {
        final String requestBody =
                "account=new-user&password=password&email=new-user%40example.com";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + requestBody.getBytes(StandardCharsets.UTF_8).length,
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);

        assertThat(InMemoryUserRepository.findByAccount("new-user")).isPresent();
        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found")
                .contains("Location: /index.html");
    }
}
