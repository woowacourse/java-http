package nextstep.jwp.presentation;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.http11.file.ResourceLoader;
import org.apache.coyote.http11.support.HttpHeader;
import org.apache.coyote.http11.support.HttpHeaders;
import org.apache.coyote.http11.support.HttpStartLine;
import org.apache.coyote.http11.support.HttpStatus;
import org.apache.coyote.http11.web.request.HttpRequest;
import org.apache.coyote.http11.web.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;

class RegisterControllerTest {

    @BeforeEach
    void setUp() {
        InMemoryUserRepository.cleanUp();
    }

    @DisplayName("회원가입 페이지를 로드한다.")
    @Test
    void render() throws IOException {
        // given
        final HttpStartLine httpStartLine =
                HttpStartLine.from(new String[]{"GET", "/register.html", "HTTP/1.1"});
        final HttpRequest httpRequest = new HttpRequest(httpStartLine, new HttpHeaders(Collections.emptyMap()), "");

        final HttpResponse expected = new HttpResponse(
                HttpStatus.OK,
                new HttpHeaders(new LinkedHashMap<>()),
                ResourceLoader.getContent("register.html")
        );

        // when
        final HttpResponse actual = new RegisterController().doGet(httpRequest);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("유효한 정보로 회원가입 시, 성공 응답을 반환한다.")
    @Test
    void register() {
        // given
        final HttpStartLine httpStartLine =
                HttpStartLine.from(new String[]{"POST", "/register", "HTTP/1.1"});
        final String requestBody = "account=sun&email=sun@gmail.com&password=qwer1234";
        final HttpRequest httpRequest =
                new HttpRequest(httpStartLine, new HttpHeaders(Collections.emptyMap()), requestBody);

        final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        httpHeaders.put(HttpHeader.LOCATION, "/index.html");
        final HttpResponse expected = new HttpResponse(
                HttpStatus.FOUND,
                httpHeaders,
                ""
        );

        // when
        final HttpResponse actual = new RegisterController().doPost(httpRequest);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("이미 존재하는 계정으로 회원가입 시, 가입시키지 않고 다시 회원가입 페이지로 리다이렉트한다.")
    @Test
    void register_returnsRedirect_ifDuplicatedUserAccount() {
        // given
        final HttpStartLine httpStartLine =
                HttpStartLine.from(new String[]{"POST", "/register", "HTTP/1.1"});
        final String requestBody = "account=gugu&email=gugu@gmail.com&password=qwer1234";
        final HttpRequest httpRequest =
                new HttpRequest(httpStartLine, new HttpHeaders(Collections.emptyMap()), requestBody);

        final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        httpHeaders.put(HttpHeader.LOCATION, "/register.html");
        final HttpResponse expected = new HttpResponse(
                HttpStatus.FOUND,
                httpHeaders,
                ""
        );

        // when
        final HttpResponse actual = new RegisterController().doPost(httpRequest);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("입력 값을 작성하지 않고 회원가입 시, 가입시키지 않고 다시 회원가입 페이지로 리다이렉트한다.")
    @ParameterizedTest
    @ValueSource(strings = {
            "account=&email=sun@gmail.com&password=qwer1234",
            "account=sun&email=&password=qwer1234",
            "account=sun&email=sun@gmail.com&password="
    })
    void register_returnsRedirect_ifInvalidRequest(final String requestBody) {
        // given
        final HttpStartLine httpStartLine =
                HttpStartLine.from(new String[]{"POST", "/register", "HTTP/1.1"});
        final HttpRequest httpRequest =
                new HttpRequest(httpStartLine, new HttpHeaders(Collections.emptyMap()), requestBody);

        final HttpHeaders httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        httpHeaders.put(HttpHeader.LOCATION, "/register.html");
        final HttpResponse expected = new HttpResponse(
                HttpStatus.FOUND,
                httpHeaders,
                ""
        );

        // when
        final HttpResponse actual = new RegisterController().doPost(httpRequest);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
