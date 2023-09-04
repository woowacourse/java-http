package nextstep.jwp.handler;

import static org.apache.coyote.http.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpStatus;
import org.apache.coyote.http.SupportFile;
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

    @Test
    void 로그인_페이지_요청_핸들러는_GET_요청시_로그인_페이지를_반환한다() throws IOException {
        // given
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(GET)
                .url(Url.from("/login"))
                .headers(HttpHeaders.getEmptyHeaders())
                .build();

        // when
        HttpResponse response = new LoginPageHandler().handle(request);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        final HttpHeaders headers = HttpHeaders.getEmptyHeaders();
        headers.put(HttpHeader.CONTENT_TYPE, SupportFile.HTML.getContentType());

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(new HttpResponse(HttpStatus.OK, headers, expectedBody));
    }

    @Test
    void 로그인_요청_핸들러는_GET_요청과_쿼리_파라미터를_지원한다() {
        // given
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.GET)
                .url(Url.from("/login?account=gugu&password=password"))
                .headers(HttpHeaders.getEmptyHeaders())
                .build();

        // when
        final LoginHandler loginHandler = new LoginHandler();

        // then
        assertThat(loginHandler.isSupported(request)).isTrue();
    }

    @Test
    void 로그인_요청_핸들러는_계정과_패스워드가_맞으면_index_리다이렉트를_반환한다() {
        // given
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.GET)
                .url(Url.from("/login?account=gugu&password=password"))
                .headers(HttpHeaders.getEmptyHeaders())
                .build();

        // when
        final LoginHandler loginHandler = new LoginHandler();
        HttpResponse response = loginHandler.handle(request);

        // then
        final HttpHeaders headers = HttpHeaders.getEmptyHeaders();
        headers.put(HttpHeader.LOCATION, "/index.html");
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(new HttpResponse(HttpStatus.REDIRECT, headers));
    }

    @Test
    void 로그인_요청_핸들러는_계정과_패스워드가_틀리면_401_리다이렉트를_반환한다() {
        // given
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.GET)
                .url(Url.from("/login?account=gugu&password=pass2word"))
                .headers(HttpHeaders.getEmptyHeaders())
                .build();

        // when
        final LoginHandler loginHandler = new LoginHandler();
        HttpResponse response = loginHandler.handle(request);

        // then
        final HttpHeaders headers = HttpHeaders.getEmptyHeaders();
        headers.put(HttpHeader.LOCATION, "/401.html");
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(new HttpResponse(HttpStatus.REDIRECT, headers));
    }
}
