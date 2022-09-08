package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.catalina.servlets.Controller;
import org.apache.catalina.servlets.RequestMappings;
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
        stubSocket.setRequest(String.join("\r\n",
                "GET / HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: " + body.getBytes().length,
                "",
                body));
        WebConfig webConfig = new WebConfig(null, new RequestMappings(List.of(new Controller() {
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
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/html",
                "",
                body);

        assertThat(stubSocket.output()).isEqualTo(expected);
    }
}
