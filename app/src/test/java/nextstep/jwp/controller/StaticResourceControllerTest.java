package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.response.ContentType;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.staticresource.StaticResource;
import nextstep.jwp.staticresource.StaticResourceFinder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("StaticResourceController 테스트")
class StaticResourceControllerTest {

    private final StaticResourceFinder staticResourceFinder = mock(StaticResourceFinder.class);
    private final StaticResourceController staticResourceController = new StaticResourceController(staticResourceFinder);

    @DisplayName("정적 리소스 GET 요청 테스트")
    @Test
    void getRegister() {
        //given
        final RequestLine requestLine = new RequestLine("GET /index.html HTTP/1.1");
        final HttpRequest request = new HttpRequest(requestLine, null, null);
        final HttpResponse response = new HttpResponse();

        final StaticResource staticResource = new StaticResource(ContentType.HTML, "content");
        given(staticResourceFinder.findStaticResource("/index.html")).willReturn(staticResource);

        //when
        staticResourceController.service(request, response);

        //then
        assertThat(response.getBytes()).isNotEmpty();
    }
}