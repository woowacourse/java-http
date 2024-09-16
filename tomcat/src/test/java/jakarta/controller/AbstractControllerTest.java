package jakarta.controller;

import jakarta.http.HttpMethod;
import jakarta.http.HttpRequest;
import jakarta.http.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AbstractControllerTest {

    @Test
    @DisplayName("get과 post 요청 아니라면 요청을 처리할 수 없다.")
    void canHandleOtherMethod() throws Exception {
        AbstractController controller = new AbstractController();

        HttpResponse response = mock(HttpResponse.class);
        HttpRequest request = mock(HttpRequest.class);
        HttpMethod method = mock(HttpMethod.class);
        when(request.getHttpMethod())
                .thenReturn(method);
        when(method.isGet())
                .thenReturn(false);
        when(method.isPost())
                .thenReturn(false);

        assertThatThrownBy(() -> controller.service(request, response))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("지원하지 않는 HTTP 메서드입니다.");
    }
}
