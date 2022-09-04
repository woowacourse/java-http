package org.apache.coyote.http11.message.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.message.common.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @DisplayName("HTTP Request Header 들을 전달하여 생성한다.")
    @Test
    void constructor() {
        // given
        String requestHeaders =
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\n"
                        + "Accept-Encoding: gzip, deflate, br\n"
                        + "Accept-Language: ko,en-US;q=0.9,en;q=0.8,zh-CN;q=0.7,zh;q=0.6,fr;q=0.5\n"
                        + "Connection: keep-alive\n"
                        + "Host: localhost:8080\n"
                        + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36";

        // when
        HttpHeaders actual = new HttpHeaders(requestHeaders);

        // then
        assertThat(actual).isNotNull();
    }

    @DisplayName("헤더를 하나 가져온다.")
    @Test
    void getHeader() {
        // given
        String requestHeaders =
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\r\n"
                        + "Accept-Encoding: gzip, deflate, br\r\n"
                        + "Accept-Language: ko,en-US;q=0.9,en;q=0.8,zh-CN;q=0.7,zh;q=0.6,fr;q=0.5\r\n"
                        + "Connection: keep-alive\r\n"
                        + "Host: localhost:8080\r\n"
                        + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36";

        HttpHeaders httpHttpHeaders = new HttpHeaders(requestHeaders);

        // when
        String actual = httpHttpHeaders.getHeader("Host").get();

        // then
        assertThat(actual).isEqualTo("localhost:8080");
    }

}
