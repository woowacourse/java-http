package nextstep.jwp.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.catalina.servlet.request.HttpRequest;
import org.apache.catalina.servlet.request.StartLine;
import org.apache.catalina.servlet.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("RootPageRequestHandler 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class RootPageRequestHandlerTest {

    private final RootPageRequestHandler handler = new RootPageRequestHandler();

    @DisplayName("Hello world! 를 반환한다")
    @Test
    void test() {
        // given
        HttpRequest request = HttpRequest.builder()
                .startLine(StartLine.from("GET / HTTP/1.1 "))
                .build();
        HttpResponse response = new HttpResponse();

        // when
        handler.handle(request, response);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(response.toString()).isEqualTo(expected);
    }
}
