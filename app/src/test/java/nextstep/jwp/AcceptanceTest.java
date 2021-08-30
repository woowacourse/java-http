package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class AcceptanceTest {

    @Test
    void getIndex() {
        //given
        final String httpRequest= String.join("\r\n",
            "GET /index.html HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String entire = socket.output();
        assertThat(socket.output()).isNotEmpty();
        assertThat(getCode(entire)).isEqualTo("200");
    }

    @Test
    void signUpSuccess() {
        //given
        final String httpRequest= String.join("\r\n",
            "POST /register HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 80",
            "Content-Type: application/x-www-form-urlencoded",
            "Accept: */*",
            "",
            "account=whybe&password=password&email=hybeom@gmail.com");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String entire = socket.output();
        assertThat(socket.output()).isNotEmpty();
        assertThat(redirectCode(entire)).isEqualTo("302");
    }

    @Test
    void signUpFail() {
        //given
        final String httpRequest= String.join("\r\n",
            "POST /register HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 80",
            "Content-Type: application/x-www-form-urlencoded",
            "Accept: */*",
            "",
            "account=gugu&password=password&email=gugu@gmail.com");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String entire = socket.output();
        assertThat(socket.output()).isNotEmpty();
        assertThat(redirectCode(entire)).isEqualTo("401");
    }

    @Test
    void staticSuccess() {
        //given
        final String httpRequest= String.join("\r\n",
            "GET /css/styles.css HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Accept: text/css,*/*;q=0.1",
            "Connection: keep-alive ",
            "",
            "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String entire = socket.output();
        assertThat(socket.output()).isNotEmpty();
        assertThat(getCode(entire)).isEqualTo("200");
    }

    private String getCode(String entireResponse) {
        String[] lines = entireResponse.split("\n");
        String[] firstHeaderLine = lines[0].split(" ");
        return firstHeaderLine[1];
    }

    private String redirectCode(String entireResponse) {
        String[] lines = entireResponse.split("\n");
        String[] firstHeaderLine = lines[4].split(" ");
        return firstHeaderLine[1];
    }
}
