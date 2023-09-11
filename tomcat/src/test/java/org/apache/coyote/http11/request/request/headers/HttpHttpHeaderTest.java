package org.apache.coyote.http11.request.request.headers;

import java.util.List;
import org.apache.coyote.http11.request.HttpHeader;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class HttpHttpHeaderTest {

    @Nested
    class HttpHttpHeader_생성을_검증한다 {

        @Test
        void 유효한_RequestHeader라면_생성한다() {
            // given
            List<String> requests = List.of(
                    "Host: www.test01.com",
                    "Accept: image/gif, image/jpeg, */*",
                    "Accept-Language: en-us",
                    "Accept-Encoding: gzip, deflate",
                    "User-Agent: Mozilla/4.0",
                    "Content-Length: 35"
            );

            // when
            HttpHeader httpHeader = HttpHeader.from(requests);

            // then
            assertAll(
                    () -> assertThat(httpHeader.headers().get("Host"))
                            .contains("www.test01.com"),
                    () -> assertThat(httpHeader.headers().get("Accept"))
                            .contains("image/gif", "image/jpeg", "*/*"),
                    () -> assertThat(httpHeader.headers().get("Accept-Language"))
                            .contains("en-us"),
                    () -> assertThat(httpHeader.headers().get("Accept-Encoding"))
                            .contains("gzip", "deflate"),
                    () -> assertThat(httpHeader.headers().get("User-Agent"))
                            .contains("Mozilla/4.0"),
                    () -> assertThat(httpHeader.headers().get("Content-Length"))
                            .contains("35")
            );
        }

    }

}
