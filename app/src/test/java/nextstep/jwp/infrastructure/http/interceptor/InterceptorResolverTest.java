package nextstep.jwp.infrastructure.http.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import nextstep.jwp.infrastructure.http.Headers;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.request.Method;
import nextstep.jwp.infrastructure.http.request.RequestLine;
import nextstep.jwp.infrastructure.http.response.HttpResponse;
import nextstep.jwp.infrastructure.http.response.ResponseLine;
import nextstep.jwp.infrastructure.http.response.StatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InterceptorResolverTest {

    private final InterceptorResolver interceptorResolver = new InterceptorResolver("nextstep.jwp.interceptor");

    @DisplayName("Session Id가 존재하지 않는 경우 Set-Cookie 헤더에 Session Id를 추가")
    @Test
    void postHandle() throws Exception {
        final HttpRequest request = new HttpRequest(
            new RequestLine(Method.GET, "/"),
            new Headers(),
            ""
        );
        final HttpResponse response = new HttpResponse();

        interceptorResolver.postHandle(request, response);
        final Headers headers = response.getHeaders();

        assertThat(headers.hasKey("Set-Cookie")).isTrue();
        assertThat(headers.getValue("Set-Cookie")).contains("JSESSIONID=");
    }
}

