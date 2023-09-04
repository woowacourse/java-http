package nextstep.jwp.protocol.request.headers;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class RequestHeadersTest {

    @Nested
    class RequestHeaders_생성을_검증한다 {

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
            RequestHeaders requestHeaders = RequestHeaders.from(requests);

            // then
            assertAll(
                    () -> assertThat(requestHeaders.headers().get("Host"))
                            .contains("www.test01.com"),
                    () -> assertThat(requestHeaders.headers().get("Accept"))
                            .contains("image/gif", "image/jpeg", "*/*"),
                    () -> assertThat(requestHeaders.headers().get("Accept-Language"))
                            .contains("en-us"),
                    () -> assertThat(requestHeaders.headers().get("Accept-Encoding"))
                            .contains("gzip", "deflate"),
                    () -> assertThat(requestHeaders.headers().get("User-Agent"))
                            .contains("Mozilla/4.0"),
                    () -> assertThat(requestHeaders.headers().get("Content-Length"))
                            .contains("35")
            );
        }

    }

}
