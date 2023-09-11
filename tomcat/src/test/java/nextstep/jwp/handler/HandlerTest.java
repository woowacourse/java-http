package nextstep.jwp.handler;

import static org.apache.coyote.http.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpStatus;
import org.apache.coyote.http.SupportFile;
import org.apache.coyote.http.vo.HttpBody;
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
        boolean supported = new DefaultPageController().isSupported(request);

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
        boolean supported = new DefaultPageController().isSupported(request);

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
        HttpResponse response = new HttpResponse();
        new DefaultPageController().service(request, response);

        // then
        assertThat(response.getRawResponse()).contains("Hello world");
    }

    @Test
    void 로그인_컨트롤러는_GET_요청시_로그인_페이지를_반환한다() throws IOException {
        // given
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(GET)
                .url(Url.from("/login"))
                .headers(HttpHeaders.getEmptyHeaders())
                .build();

        // when
        HttpResponse response = new HttpResponse();
        new LoginController().service(request, response);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(new HttpResponse()
                        .setStatus(HttpStatus.OK)
                        .setHeader(HttpHeader.CONTENT_TYPE, SupportFile.HTML.getContentType())
                        .setBody(expectedBody)
                );
    }

    @Test
    void 로그인_요청_핸들러는_GET_요청과_쿼리_파라미터를_지원한다() {
        // given
        HttpHeaders headers = HttpHeaders.getEmptyHeaders();
        HttpBody body = HttpBody.getEmptyBody();

        headers.put(HttpHeader.CONTENT_LENGTH, "50");
        body.put("account", "gugu");
        body.put("password", "password");
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.POST)
                .url(Url.from("/login"))
                .headers(HttpHeaders.getEmptyHeaders())
                .body(body)
                .build();

        // when
        final LoginController loginController = new LoginController();

        // then
        assertThat(loginController.isSupported(request)).isTrue();
    }

    @Test
    void 로그인_요청_핸들러는_계정과_패스워드가_맞으면_세션값과_index_리다이렉트를_반환한다() {
        // given
        HttpHeaders headers = HttpHeaders.getEmptyHeaders();
        HttpBody body = HttpBody.getEmptyBody();

        headers.put(HttpHeader.CONTENT_LENGTH, "50");
        body.put("account", "gugu");
        body.put("password", "password");
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.POST)
                .url(Url.from("/login"))
                .headers(HttpHeaders.getEmptyHeaders())
                .body(body)
                .build();

        // when
        HttpResponse response = new HttpResponse();
        new LoginController().service(request, response);

        // then
        assertAll(
                () -> assertThat(response).usingRecursiveComparison()
                        .ignoringFields("cookie")
                        .isEqualTo(new HttpResponse()
                                .setStatus(HttpStatus.REDIRECT)
                                .setHeader(HttpHeader.LOCATION, "/index.html")
                        ),
                () -> assertThat(response.getRawResponse()).contains("JSESSIONID")
        );
    }

    @Test
    void 로그인_요청_핸들러는_계정과_패스워드가_틀리면_401_리다이렉트를_반환한다() throws IOException {
        // given
        HttpHeaders headers = HttpHeaders.getEmptyHeaders();
        HttpBody body = HttpBody.getEmptyBody();

        headers.put(HttpHeader.CONTENT_LENGTH, "50");
        body.put("account", "gugu2");
        body.put("password", "password");
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.POST)
                .url(Url.from("/login"))
                .headers(HttpHeaders.getEmptyHeaders())
                .body(body)
                .build();

        // when
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        final String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        HttpResponse response = new HttpResponse();
        new LoginController().service(request, response);

        // then
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(new HttpResponse()
                        .setStatus(HttpStatus.UNAUTHORIZED)
                        .setHeader(HttpHeader.CONTENT_TYPE, SupportFile.HTML.getContentType())
                        .setBody(expectedBody)
                );
    }

    @Test
    void 회원가입_컨트롤러는_GET_요청과_register_경로를_지원한다() {
        // given
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.GET)
                .url(Url.from("/register"))
                .headers(HttpHeaders.getEmptyHeaders())
                .build();

        // when
        boolean supported = new RegisterController().isSupported(request);

        //then
        assertThat(supported).isTrue();
    }

    @Test
    void 회원가입_페이지_요청_핸들러는_GET_요청시_회원가입_페이지를_반환한다() throws IOException {
        // given
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.GET)
                .url(Url.from("/register"))
                .headers(HttpHeaders.getEmptyHeaders())
                .build();

        // when
        HttpResponse response = new HttpResponse();
        new RegisterController().service(request, response);

        //then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(new HttpResponse()
                        .setStatus(HttpStatus.OK)
                        .setHeader(HttpHeader.CONTENT_TYPE, SupportFile.HTML.getContentType())
                        .setBody(expectedBody)
                );
    }

    @Test
    void 유저_생성_요청_핸들러는_POST와_적절한_Body를_가진_요청을_지원한다() {
        // given
        HttpHeaders headers = HttpHeaders.getEmptyHeaders();
        HttpBody body = HttpBody.getEmptyBody();

        headers.put(HttpHeader.CONTENT_LENGTH, "50");
        body.put("account", "gugu2");
        body.put("email", "gugu@gmail.com");
        body.put("password", "password");

        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.POST)
                .url(Url.from("/register"))
                .headers(headers)
                .body(body)
                .build();

        // when
        final RegisterController registerController = new RegisterController();

        // then
        assertThat(registerController.isSupported(request)).isTrue();
    }

    @Test
    void 유저_생성_요청_핸들러는_성공시_로그인_페이지를_반환한다() {
        // given
        HttpHeaders headers = HttpHeaders.getEmptyHeaders();
        HttpBody body = HttpBody.getEmptyBody();

        headers.put(HttpHeader.CONTENT_LENGTH, "50");
        body.put("account", "gugu2");
        body.put("email", "gugu@gmail.com");
        body.put("password", "password");

        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.POST)
                .url(Url.from("/register"))
                .headers(headers)
                .body(body)
                .build();

        // when
        HttpResponse response = new HttpResponse();
        new RegisterController().service(request, response);

        // then
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(new HttpResponse()
                        .setStatus(HttpStatus.REDIRECT)
                        .setHeader(HttpHeader.LOCATION, "/login")
                );
    }

    @Test
    void 유저_생성_요청_핸들러는_실패시_Bad_Request_를_반환한다() throws IOException {
        // given
        HttpHeaders headers = HttpHeaders.getEmptyHeaders();
        HttpBody body = HttpBody.getEmptyBody();

        headers.put(HttpHeader.CONTENT_LENGTH, "50");
        body.put("account", "gugu");
        body.put("email", "gugu@gmail.com");
        body.put("password", "password");

        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(HttpMethod.POST)
                .url(Url.from("/register"))
                .headers(headers)
                .body(body)
                .build();

        // when
        final URL resource = getClass().getClassLoader().getResource("static/400.html");
        final String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        HttpResponse response = new HttpResponse();
        new RegisterController().service(request, response);

        // then
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(new HttpResponse()
                        .setStatus(HttpStatus.BAD_REQUEST)
                        .setHeader(HttpHeader.CONTENT_TYPE, SupportFile.HTML.getContentType())
                        .setBody(expectedBody)
                );
    }
}
