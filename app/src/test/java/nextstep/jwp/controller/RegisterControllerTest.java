package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestBody;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.response.ContentType;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;
import nextstep.jwp.service.RegisterService;
import nextstep.jwp.staticresource.StaticResource;
import nextstep.jwp.staticresource.StaticResourceFinder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;

@DisplayName("RegisterController 테스트")
class RegisterControllerTest {

    private final StaticResourceFinder staticResourceFinder = mock(StaticResourceFinder.class);
    private final RegisterService registerService = mock(RegisterService.class);
    private final RegisterController registerController = new RegisterController(staticResourceFinder, registerService);

    @DisplayName("회원가입 페이지 GET 요청 테스트")
    @Test
    void getRegister() {
        //given
        final RequestLine requestLine = new RequestLine("GET /register HTTP/1.1");
        final HttpRequest request = new HttpRequest(requestLine, null, null);
        final HttpResponse response = new HttpResponse();
        final StaticResource staticResource = new StaticResource(ContentType.HTML, "content");

        given(staticResourceFinder.findStaticResource("/register.html")).willReturn(staticResource);

        //when
        registerController.service(request, response);

        //then
        assertThat(response.getBytes()).isNotEmpty();
    }

    @DisplayName("회원가입 POST 요청 테스트")
    @Test
    void postRegister() {
        //given
        final RequestLine requestLine = new RequestLine("POST /register HTTP/1.1");
        final RequestBody requestBody = new RequestBody("account=inbi&email=inbi@email.com&password=1234");
        final HttpRequest request = new HttpRequest(requestLine, null, requestBody);
        final HttpResponse response = new HttpResponse();

        willDoNothing().given(registerService).register(any(User.class));

        //when
        registerController.service(request, response);

        //then
        assertThat(response.getBytes()).isNotEmpty();
    }
}