package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestCookie;
import nextstep.jwp.http.request.RequestHeaders;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.response.ContentType;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.staticresource.StaticResource;
import nextstep.jwp.staticresource.StaticResourceFinder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("HomeController 테스트")
class HomeControllerTest {

    private final StaticResourceFinder staticResourceFinder = mock(StaticResourceFinder.class);
    private final HomeController homeController = new HomeController(staticResourceFinder);

    @DisplayName("루트 경로 GET 요청 테스트")
    @Test
    void getHome() {
        //given
        final RequestLine requestLine = new RequestLine("GET / HTTP/1.1");
        final RequestCookie requestCookie = new RequestCookie(new HashMap<>());
        final RequestHeaders headers = new RequestHeaders(new HashMap<>(), requestCookie);
        final HttpRequest request = new HttpRequest(requestLine, headers, null);
        final HttpResponse response = new HttpResponse();
        final StaticResource staticResource = new StaticResource(ContentType.HTML, "content");

        given(staticResourceFinder.findStaticResource("/index.html")).willReturn(staticResource);

        //when
        homeController.service(request, response);

        //then
        assertThat(response.getBytes()).isNotEmpty();
    }
}
