package servlet.http.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ResponseHeadersTest {

    @Test
    void ResponseHeaders를_조립한다() {
        // given
        ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.setContentType("text/html");
        responseHeaders.setContentLength(1024);

        // when
        StringBuilder builder = new StringBuilder();
        responseHeaders.assemble(builder);

        // then
        String expected = """
                Content-Type: text/html;charset=utf-8 \r
                Content-Length: 1024 \r
                \r
                """;
        assertThat(builder.toString()).isEqualTo(expected);
    }

    @Test
    void 쿠키가_있다면_Set_Cookie_헤더도_함께_조립한다() {
        // given
        ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.setContentType("text/html");
        responseHeaders.setContentLength(1024);
        responseHeaders.setJsessionid("656cef62-e3c4-40bc-a8df-94732920ed46");

        // when
        StringBuilder builder = new StringBuilder();
        responseHeaders.assemble(builder);

        // then
        String expected = """
                Set-Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 \r
                Content-Type: text/html;charset=utf-8 \r
                Content-Length: 1024 \r
                \r
                """;
        assertThat(builder.toString()).isEqualTo(expected);
    }
}
