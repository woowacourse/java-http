package com.techcourse.presentation;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.techcourse.db.InMemoryUserRepository;
import java.util.Map;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http.request.RequestHeaders;
import org.apache.coyote.http.request.RequestLine;
import org.apache.coyote.http.response.Response;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    private final RegisterController registerController = RegisterController.getInstance();

    @Test
    void POST_요청_시_회원가입_후_index_경로로_리다이렉트한다() {
        // given
        RequestLine requestLine = new RequestLine("POST /register HTTP/1.1");
        Map<String, String> headers = Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive",
                "Content-Type", "application/x-www-form-urlencoded",
                "Content-Length", "53"
        );
        RequestHeaders requestHeaders = new RequestHeaders(headers);
        RequestBody requestBody = RequestBody.from("account=prin&password=password&email=prin@gmail.com");
        Request request = new Request(requestLine, requestHeaders, requestBody);
        Response response = new Response();

        // when
        registerController.register(request, response);

        // then
        assertSoftly(softly -> {
            softly.assertThat(response.getViewName()).isEqualTo("/index");
            softly.assertThat(InMemoryUserRepository.findByAccount("prin")).isNotNull();
        });
    }
}
