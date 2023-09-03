package org.apache.coyote.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpMethod;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class HttpRequestTest {

    @Test
    void http_요청을__메서드_요청경로_헤더로_파싱한다() {
        // given
        final String rawRequest = "POST /login?name=gugu&password=1234 HTTP/1.1\n"
                + "Host: localhost:8080\n"
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
        final HttpRequest httpRequest = HttpRequest.of(rawRequest);

        // then
        assertAll(
                () -> assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.POST),
                () -> assertThat(httpRequest.getUrl().getUrlPath()).isEqualTo("/login"),
                () -> assertThat(httpRequest.getUrl().getQueryStrings().getQueryString("name")).isEqualTo("gugu"),
                () -> assertThat(httpRequest.getUrl().getQueryStrings().getQueryString("password")).isEqualTo("1234"),
                () -> assertThat(httpRequest.getHeaders().getHeaderValues(HttpHeader.ACCEPT)).hasSize(8),
                () -> assertThat(httpRequest.getHeaders().getHeaderValues(HttpHeader.ACCEPT)).isEqualTo(
                        List.of("text/html",
                                "application/xhtml+xml",
                                "application/xml;q=0.9",
                                "image/avif",
                                "image/webp",
                                "image/apng",
                                "*/*;q=0.8",
                                "application/signed-exchange;v=b3;q=0.7")
                )
        );
    }
}
