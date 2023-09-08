package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.jwp.FileFinder;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestLine;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class RegisterControllerTest {

    private final RegisterController registerController = new RegisterController();

    @Test
    void 회원가입을_성공한다() throws Exception {
        // given
        HttpRequest request = createRequest(HttpMethod.POST, "account=blackcat&password=dntjr159&email=email@naver.com");
        HttpResponse response = new HttpResponse();

        // when
        registerController.doPost(request, response);

        // then
        User actual = InMemoryUserRepository.findByAccount("blackcat").get();
        assertAll(
            () -> assertSavedUser(actual),
            () -> assertRedirectToIndex(response)
        );
    }

    @Test
    void 회원가입_페이지로_이동한다() throws Exception {
        // given
        HttpRequest request = createRequest(HttpMethod.GET, "");
        HttpResponse response = new HttpResponse();

        // when
        registerController.doGet(request, response);

        // then
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(FileFinder.getFileContent("/register.html"));
    }

    private HttpRequest createRequest(HttpMethod method, String body) {
        return new HttpRequest(
            new HttpRequestLine(method, "/register", "version"),
            null,
            null,
            body
        );
    }

    private void assertSavedUser(User actual) {
        assertAll(
            () -> assertThat(actual.getAccount()).isEqualTo("blackcat"),
            () -> assertThat(actual.getPassword()).isEqualTo("dntjr159"),
            () -> assertThat(actual.getEmail()).isEqualTo("email@naver.com")
        );
    }

    private void assertRedirectToIndex(HttpResponse response) {
        assertAll(
            () -> assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.FOUND),
            () -> assertThat(response.getHeaders().get(HttpHeaders.LOCATION)).isEqualTo("/index.html")
        );
    }
}
