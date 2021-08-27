package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.response.ContentType;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.staticresource.StaticResource;
import nextstep.jwp.staticresource.StaticResourceFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DisplayName("StaticResourceController 테스트")
class StaticResourceControllerTest {

    @Mock
    private StaticResourceFinder staticResourceFinder;
    @InjectMocks
    private StaticResourceController staticResourceController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

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