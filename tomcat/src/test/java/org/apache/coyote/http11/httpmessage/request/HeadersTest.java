package org.apache.coyote.http11.httpmessage.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.Headers;
import org.apache.coyote.http11.session.Cookie;
import org.junit.jupiter.api.Test;

class HeadersTest {

    @Test
    void header가_존재하지_않는_경우_빈_값이_저장된다() {
        // given
        List<String> headers = List.of();

        // when
        Headers requestHeader = Headers.of(headers);

        // then
        assertThat(requestHeader).extracting("headers")
                .isEqualTo(Map.of());
    }

    @Test
    void header_입력값들의_key_value를_구분할_수_있다() {
        // given
        List<String> headers = List.of("key: value", "name: park");

        // when
        Headers requestHeader = Headers.of(headers);

        // then
        assertThat(requestHeader).extracting("headers")
                .isEqualTo(Map.of("key", "value", "name", "park"));
    }

    @Test
    void header_입력값의_형식이_잘못된_경우_예외를_던진다() {
        // given
        List<String> headers = List.of("key:value", "name: park");

        // when & then
        assertThatThrownBy(() -> Headers.of(headers))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    void 다양한_header들을_추가할_수_있다() {
        // given
        Headers headers = Headers.of(List.of("key: value", "name: park"));

        // when
        headers.addSetCookie(Cookie.of("123=456"));
        headers.addContentLength(3);
        headers.addLocation("/123.html");
        headers.addContentType(ContentType.HTML);

        // then
        assertThat(headers.toString())
                .isEqualTo(
                        String.join("\r\n",
                                "key: value ",
                                "name: park ",
                                "Set-Cookie: 123=456 ",
                                "Content-Length: 3 ",
                                "Location: /123.html ",
                                "Content-Type: text/html;charset=utf-8 "
                        ));
    }
}
