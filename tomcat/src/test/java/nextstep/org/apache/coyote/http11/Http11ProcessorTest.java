package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.coyote.Controller;
import org.apache.coyote.ControllerMappings;
import org.apache.coyote.WebConfig;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.spec.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    @DisplayName("HTTP 요청 메시지를 읽는다.")
    void readHttpRequest() {
        // given
        StubSocket stubSocket = new StubSocket();
        String body = "body message";
        stubSocket.setRequest("GET / HTTP/1.1\r\nHost: localhost:8080\r\n\r\n" + body + "\r\n");
        WebConfig webConfig = new WebConfig(null, new ControllerMappings(List.of(new Controller() {
            @Override
            public boolean isProcessable(HttpRequest request) {
                return true;
            }

            @Override
            public void service(HttpRequest request, HttpResponse response) {
                response.setStatus(HttpStatus.OK);
                response.addHeader("Content-Type", "text/html");
                response.setBody(request.getBody());
            }
        })));
        Http11Processor processor = new Http11Processor(stubSocket, webConfig);

        // when
        processor.process(stubSocket);

        // then
        String expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                "\r\n" +
                body;

        assertThat(stubSocket.output()).isEqualTo(expected);
    }
}
