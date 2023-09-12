package nextstep.jwp.handler;

import static org.assertj.core.api.Assertions.assertThat;

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
class RegisterControllerTest {

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
    void 회원가입_컨트롤러_GET_요청시_회원가입_페이지를_반환한다() throws IOException {
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
    void 회원가입_컨트롤러는_POST와_적절한_Body를_가진_요청을_지원한다() {
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
    void 회원가입_컨트롤러는_성공시_로그인_페이지를_반환한다() {
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
    void 회원가입_컨트롤러는_중복아이디라면_Bad_Request_를_반환한다() throws IOException {
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

    @Test
    void 회원가입_컨트롤러는_reqeust_body가_다르면_Bad_Request_를_반환한다() throws IOException {
        // given
        HttpHeaders headers = HttpHeaders.getEmptyHeaders();
        HttpBody body = HttpBody.getEmptyBody();

        headers.put(HttpHeader.CONTENT_LENGTH, "50");
        body.put("acc", "gugu");
        body.put("email", "gugu@gmail.com");
        body.put("pass", "password");

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
