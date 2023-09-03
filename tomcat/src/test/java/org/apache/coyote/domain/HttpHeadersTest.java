package org.apache.coyote.domain;

import static org.apache.coyote.http.HttpHeader.ACCEPT;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.coyote.http.vo.HttpHeaders;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class HttpHeadersTest {

    @Test
    void 헤더를_파싱하여_사용하는_헤더만_Key_Value_형태로_저장한다() {
        // given
        final String rawHeaders = "Host: localhost:8080\n"
                + "Connection: keep-alive\n"
                + "sec-ch-ua: \"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Google Chrome\";v=\"116\"\n"
                + "sec-ch-ua-mobile: ?0\n"
                + "sec-ch-ua-platform: \"macOS\"\n"
                + "Upgrade-Insecure-Requests: 1\n"
                + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36\n"
                + "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\n"
                + "Purpose: prefetch\n"
                + "Sec-Fetch-Site: none\n"
                + "Sec-Fetch-Mode: navigate\n"
                + "Sec-Fetch-User: ?1\n"
                + "Sec-Fetch-Dest: document\n"
                + "Accept-Encoding: gzip, deflate, br\n"
                + "Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7\n"
                + "Cookie: Idea-a0a07ecd=5592d2ef-3b6a-449b-a2ea-89e567e94b44";

        // when
        final HttpHeaders httpHeaders = HttpHeaders.of(rawHeaders);

        // then
        assertThat(httpHeaders.getHeaderValues(ACCEPT)).hasSize(8);
        assertThat(httpHeaders.getHeaderValues(ACCEPT)).isEqualTo(
                List.of("text/html",
                        "application/xhtml+xml",
                        "application/xml;q=0.9",
                        "image/avif",
                        "image/webp",
                        "image/apng",
                        "*/*;q=0.8",
                        "application/signed-exchange;v=b3;q=0.7")
        );
    }
}
