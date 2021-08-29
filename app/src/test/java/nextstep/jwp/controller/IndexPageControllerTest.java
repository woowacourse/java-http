package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import nextstep.TestUtil;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.ResponseStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IndexPageControllerTest {

    @DisplayName("/index.html을 요청하면 OK 응답을 반환한다.")
    @Test
    void doService() throws IOException {
        Controller controller = new IndexPageController();
        HttpRequest request = TestUtil.createRequest("GET /index.html HTTP/1.1");

        HttpResponse httpResponse = controller.doService(request);
        assertThat(httpResponse.getResponseStatus()).isEqualTo(ResponseStatus.OK);
    }
}