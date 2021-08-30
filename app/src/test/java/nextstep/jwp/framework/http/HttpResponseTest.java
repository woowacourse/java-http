package nextstep.jwp.framework.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.framework.util.ResourceUtils;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    @DisplayName("HttpResponse 를 문자열로 변환 테스트")
    @Test
    void readAsStringTest() {

        // given
        StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, HttpStatus.OK);
        HttpHeaders httpHeaders = new HttpHeaders().addHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8");
        HttpResponse httpResponse = new HttpResponse.Builder().statusLine(statusLine)
                                                              .httpHeaders(httpHeaders)
                                                              .contentLength(5564)
                                                              .build();

        // when
        final String response = httpResponse.readAsString();

        // then
        String expected = String.join("\r\n", "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 5564 ",
                "",
                "");

        assertThat(response).isEqualTo(expected);
    }

    @DisplayName("리스폰스 바디를 제외한 HttpResponse 를 문자열로 변환 테스트")
    @Test
    void readAfterExceptBodyTest() {

        // given
        StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, HttpStatus.OK);
        HttpHeaders httpHeaders = new HttpHeaders().addHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8");
        HttpResponse httpResponse = new HttpResponse.Builder().statusLine(statusLine)
                                                              .httpHeaders(httpHeaders)
                                                              .body(ResourceUtils.readString("nextstep.txt"))
                                                              .build();

        // when
        final String response = httpResponse.readAfterExceptBody();

        // then
        String expected = String.join("\r\n", "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 8 ",
                "",
                "");

        assertThat(response).isEqualTo(expected);
    }
}
