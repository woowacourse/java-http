package servlet;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http.request.RequestHeaders;
import org.apache.coyote.http.request.RequestLine;
import org.junit.jupiter.api.Test;
import servlet.handler.Handler;
import servlet.handler.WelcomeHandler;

class RequestMappingInfoTest {

    @Test
    void path와_http_method가_일치할_경우_handler를_반환한다() {
        // given
        RequestLine requestLine = new RequestLine("GET / HTTP/1.1");
        RequestHeaders requestHeaders = new RequestHeaders(new HashMap<>());
        RequestBody requestBody = RequestBody.from(null);
        Request request = new Request(requestLine, requestHeaders, requestBody);

        RequestMappingInfo mappingInfo = new RequestMappingInfo("/", HttpMethod.GET, WelcomeHandler.getInstance());

        // when
        Handler handler = mappingInfo.getHandler(request);

        // then
        assertThat(handler).isExactlyInstanceOf(WelcomeHandler.class);
    }

    @Test
    void path와_http_method가_일치하지_않을_경우_null을_반환한다() {
        // given
        RequestLine requestLine = new RequestLine("GET / HTTP/1.1");
        RequestHeaders requestHeaders = new RequestHeaders(new HashMap<>());
        RequestBody requestBody = RequestBody.from(null);
        Request request = new Request(requestLine, requestHeaders, requestBody);

        RequestMappingInfo mappingInfo = new RequestMappingInfo("/login", HttpMethod.GET, WelcomeHandler.getInstance());

        // when
        Handler handler = mappingInfo.getHandler(request);

        // then
        assertThat(handler).isNull();
    }
}
