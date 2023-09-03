package nextstep.jwp.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.coyote.http11.request.Body;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestHeaders;
import org.apache.coyote.http11.request.StartLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("SignUpRequestHandler 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class SignUpRequestHandlerTest {

    private final SignUpRequestHandler handler = new SignUpRequestHandler();

    @Test
    void POST_이면서_register으로_들어온_요청만_처리한다() {
        // given
        HttpRequest onlyPost = HttpRequest.builder()
                .startLine(StartLine.from("POST /dd HTTP/1.1"))
                .build();
        HttpRequest onlyRegister = HttpRequest.builder()
                .startLine(StartLine.from("GET /register HTTP/1.1"))
                .build();
        HttpRequest match = HttpRequest.builder()
                .startLine(StartLine.from("POST /register HTTP/1.1"))
                .build();

        // when & then
        assertThat(handler.canHandle(onlyPost)).isFalse();
        assertThat(handler.canHandle(onlyRegister)).isFalse();
        assertThat(handler.canHandle(match)).isTrue();
    }

    @Test
    void 회원가입에_성공하면_index_html로_redirect() {
        // given
        HttpRequest request = HttpRequest.builder()
                .startLine(StartLine.from("POST /register HTTP/1.1"))
                .headers(RequestHeaders.from(List.of(
                                "Content-Type: application/x-www-form-urlencoded",
                                "Content-Length: 55"
                        ))
                ).body(new Body("account=mallang&email=mallang%40naver.com&password=1234"))
                .build();

        // when
        HttpResponse response = handler.handle(request);

        // then
        var expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: /index.html \r\n" +
                "\r\n";

        assertThat(response.toString()).isEqualTo(expected);
    }
}
