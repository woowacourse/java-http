package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticFileControllerTest {

    @DisplayName("index.html 파일 로딩 확인")
    @Test
    void getIndex() throws Exception {
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Host", "localhost:8080");
        requestHeaders.put("Connection", "keep-alive");
        // 테스트 추가하기
        HttpRequest httpRequest = new HttpRequest(null, null, null);

        StaticFileController staticFileController = new StaticFileController();

        HttpResponse httpResponse = staticFileController.doGet(httpRequest, new HttpResponse());

        assertThat(httpResponse.getStatus()).isEqualTo(Status.OK);
    }
}
