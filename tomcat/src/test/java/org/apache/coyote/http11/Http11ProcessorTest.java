package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket("GET / HTTP/1.1\r\nCookie: JSESSIONID=existing\r\n\r\n");
        final var processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=existing",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }
    @Test
    void issuesCookieOnlyWhenMissingForEachResponseType() {
        String[] requests = {
                "GET / HTTP/1.1",
                "GET /missing-file HTTP/1.1",
                "POST /login HTTP/1.1",
                "POST /register HTTP/1.1"
        };
        String[] statuses = {"200", "404", "302", "302"};
        String body = "account=cookietest&password=test&email=test@example.com";

        for (int i = 0; i < requests.length; i++) {
            for (boolean hasCookie : new boolean[]{false, true}) {
                String request = requests[i] + "\r\nContent-Length: " + body.length() + "\r\n"
                        + (hasCookie ? "Cookie: JSESSIONID=existing\r\n" : "")
                        + "\r\n" + body;
                StubSocket socket = new StubSocket(request);
                new Http11Processor(socket, new SessionManager()).process(socket);
                String response = socket.output();
                String headers = response.substring(0, response.indexOf("\r\n\r\n"));

                assertThat(response).startsWith("HTTP/1.1 " + statuses[i]);
                if (hasCookie) {
                    assertThat(headers).doesNotContain("Set-Cookie:");
                } else {
                    String cookieHeader = headers.lines()
                            .filter(line -> line.startsWith("Set-Cookie: "))
                            .findFirst().orElseThrow();
                    assertThat(cookieHeader).startsWith("Set-Cookie: JSESSIONID=").endsWith("; Path=/");
                    String id = cookieHeader.substring("Set-Cookie: JSESSIONID=".length(),
                            cookieHeader.indexOf(";"));
                    java.util.UUID.fromString(id);
                }
                if (statuses[i].equals("302")) {
                    assertThat(headers).contains("Content-Length: 0");
                    assertThat(response.substring(response.indexOf("\r\n\r\n") + 4)).isEmpty();
                }
            }
        }
    }
}
