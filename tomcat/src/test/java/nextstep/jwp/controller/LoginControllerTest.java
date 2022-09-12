package nextstep.jwp.controller;

import static nextstep.jwp.utils.FileUtils.getResource;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.utils.FileUtils;
import org.apache.http.ContentType;
import org.apache.http.Cookies;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpMethod;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.QueryParams;
import org.apache.http.StatusCode;
import org.apache.session.Session;
import org.apache.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @Test
    @DisplayName("세션이 유효하면 ResponseBody로 index.html을 반환한다.")
    void handleGet_valid_session() {
        Session session = SessionManager.generateNewSession();
        SessionManager.add(session);
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/login", "/login",
            QueryParams.empty(),
            HttpHeaders.parse(List.of("Cookies: JSESSIONID=" + session.getId())), null);
        LoginController loginController = new LoginController();

        HttpResponse actual = loginController.handleGet(httpRequest);

        HttpResponse expected = HttpResponse.of(StatusCode.OK, ContentType.TEXT_HTML,
            FileUtils.readFile(getResource("/index.html")));
        assertThat(actual).usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("세션이 유효하지 않으면 ResponseBody로 login.html을 반환한다.")
    void handleGet_inValid_session() {
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/login", "/login",
            QueryParams.empty(), HttpHeaders.parse(List.of()), null);
        LoginController loginController = new LoginController();

        HttpResponse actual = loginController.handleGet(httpRequest);

        HttpResponse expected = HttpResponse.of(StatusCode.OK, ContentType.TEXT_HTML,
            FileUtils.readFile(getResource("/login.html")));
        assertThat(actual).usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인에 성공한다.")
    void handlePost_login_success() {
        InMemoryUserRepository.save(new User("leo", "password", "leo@gmail.com"));
        User user = InMemoryUserRepository.findByAccount("leo")
            .orElseThrow();
        String requestBody = "account=gugu&password=password";
        HttpRequest httpRequest = new HttpRequest(HttpMethod.POST, "/login", "login",
            QueryParams.empty(), HttpHeaders.parse(List.of("Accept: text/html,*/*;q=0.1",
            "Content-Type: application/x-www-form-urlencoded")), requestBody);
        Session session = SessionManager.generateNewSession();
        session.createAttribute("user", user);
        SessionManager.add(session);
        Cookies cookies = Cookies.fromJSessionId(session.getId());
        LoginController loginController = new LoginController();

        HttpResponse actual = loginController.handlePost(httpRequest);

        HttpResponse expected = HttpResponse.of(StatusCode.FOUND, ContentType.TEXT_HTML,
            FileUtils.readFile(getResource("/index.html")), cookies);
        assertThat(actual).usingRecursiveComparison()
            .ignoringFieldsOfTypes(Cookies.class)
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인에 실패한다.")
    void handlePost_login_fail() {
        String requestBody = "account=invaild&password=invalid";
        HttpRequest httpRequest = new HttpRequest(HttpMethod.POST, "/login", "login",
            QueryParams.empty(), HttpHeaders.parse(List.of("Accept: text/html,*/*;q=0.1",
            "Content-Type: application/x-www-form-urlencoded")), requestBody);
        LoginController loginController = new LoginController();

        HttpResponse actual = loginController.handlePost(httpRequest);

        HttpResponse expected = HttpResponse.of(StatusCode.UNAUTHORIZED, ContentType.TEXT_HTML,
            FileUtils.readFile(getResource("/401.html")));
        assertThat(actual).usingRecursiveComparison()
            .isEqualTo(expected);
    }
}
