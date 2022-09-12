package nextstep.jwp.controller;

import static nextstep.jwp.utils.FileUtils.getResource;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.jwp.utils.FileUtils;
import org.apache.http.ContentType;
import org.apache.http.Cookies;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpMethod;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.QueryParams;
import org.apache.http.StatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    @Test
    @DisplayName("Get 메서드는 ResponseBody로 register.html를 반환한다.")
    void handleGet() {
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/register", "/register",
            QueryParams.empty(),
            HttpHeaders.parse(List.of()), null);
        RegisterController registerController = new RegisterController();

        HttpResponse actual = registerController.handleGet(httpRequest);

        HttpResponse expected = HttpResponse.of(StatusCode.OK, ContentType.TEXT_HTML,
            FileUtils.readFile(getResource("/register.html")));
        assertThat(actual).usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("Post 메서드는 올바른 정보를 입력받으면 회원가입을 한다.")
    void handlePost() {
        String requestBody = "account=leo&email=leo@gmail.com&password=password";
        HttpRequest httpRequest = new HttpRequest(HttpMethod.POST, "/register", "register",
            QueryParams.empty(), HttpHeaders.parse(List.of("Content-Type: application/x-www-form-urlencoded")),
            requestBody);
        RegisterController registerController = new RegisterController();

        HttpResponse actual = registerController.handlePost(httpRequest);

        HttpResponse expected = HttpResponse.of(StatusCode.CREATED, ContentType.TEXT_HTML,
            FileUtils.readFile(getResource("/index.html")));
        assertThat(actual).usingRecursiveComparison()
            .ignoringFieldsOfTypes(Cookies.class)
            .isEqualTo(expected);

    }
}
