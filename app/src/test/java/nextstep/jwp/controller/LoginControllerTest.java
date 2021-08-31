package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestBody;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.response.ContentType;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.staticresource.StaticResource;
import nextstep.jwp.staticresource.StaticResourceFinder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;

@DisplayName("LoginController 테스트")
class LoginControllerTest {

    private final StaticResourceFinder staticResourceFinder = mock(StaticResourceFinder.class);
    private final LoginService loginService = mock(LoginService.class);
    private final LoginController loginController = new LoginController(staticResourceFinder, loginService);

    @DisplayName("로그인 페이지 GET 요청 테스트")
    @Test
    void getLogin() {
        //given
        final RequestLine requestLine = new RequestLine("GET /login HTTP/1.1");
        final HttpRequest request = new HttpRequest(requestLine, null, null);
        final HttpResponse response = new HttpResponse();
        final StaticResource staticResource = new StaticResource(ContentType.HTML, "content");

        given(staticResourceFinder.findStaticResource("/login.html")).willReturn(staticResource);

        //when
        loginController.service(request, response);

        //then
        assertThat(response.getBytes()).isNotEmpty();
    }

    @DisplayName("로그인 POST 요청 테스트")
    @Test
    void postLogin() {
        //given
        final RequestLine requestLine = new RequestLine("POST /login HTTP/1.1");
        final RequestBody requestBody = new RequestBody("account=inbi&password=1234");
        final HttpRequest request = new HttpRequest(requestLine, null, requestBody);
        final HttpResponse response = new HttpResponse();

        willDoNothing().given(loginService).login(any(String.class), any(String.class));

        //when
        loginController.service(request, response);

        //then
        assertThat(response.getBytes()).isNotEmpty();
    }
}
