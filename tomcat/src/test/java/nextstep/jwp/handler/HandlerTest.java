package nextstep.jwp.handler;

import static org.apache.coyote.http.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http.vo.HttpHeaders;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.HttpResponse;
import org.apache.coyote.http.vo.Url;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class HandlerTest {

    @Test
    void 기본_페이지_핸들러는_기본_URL_를_지원한다() {
        // given
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(GET)
                .url(Url.from("/"))
                .headers(HttpHeaders.getEmptyHeaders())
                .build();

        // when
        boolean supported = new DefaultPageHandler().isSupported(request);

        // then
        assertThat(supported).isTrue();
    }

    @Test
    void 기본_페이지_핸들러는_기본_URL이_아니면_지원하지않는다() {
        // given
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(GET)
                .url(Url.from("/index.html"))
                .headers(HttpHeaders.getEmptyHeaders())
                .build();

        String urlPath = request.getUrl().getUrlPath();
        System.out.println(urlPath);
        // when
        boolean supported = new DefaultPageHandler().isSupported(request);

        // then
        assertThat(supported).isFalse();
    }

    @Test
    void 기본_페이지_핸들러는_GET_요청시_Hello_World_를_반환한다() {
        // given
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(GET)
                .url(Url.from("/"))
                .headers(HttpHeaders.getEmptyHeaders())
                .build();

        // when
        HttpResponse response = new DefaultPageHandler().handle(request);

        // then
        assertThat(response.getRawResponse()).contains("Hello world");
    }
}
