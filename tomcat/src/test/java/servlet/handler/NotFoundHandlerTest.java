package servlet.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http.request.RequestHeaders;
import org.apache.coyote.http.request.RequestLine;
import org.apache.coyote.http.response.Response;
import org.junit.jupiter.api.Test;

class NotFoundHandlerTest {

    private final NotFoundHandler notFoundHandler = NotFoundHandler.getInstance();

    @Test
    void notFound_경로를_반환한다() {
        // given
        RequestLine requestLine = new RequestLine("GET /any HTTP/1.1");
        Map<String, String> headers = Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive"
        );
        RequestHeaders requestHeaders = new RequestHeaders(headers);
        RequestBody requestBody = RequestBody.from(null);
        Request request = new Request(requestLine, requestHeaders, requestBody);
        Response response = new Response();

        // when
        notFoundHandler.handleRequest(request, response);

        // then
        assertThat(response.getViewName()).isEqualTo("/404");
    }
}
