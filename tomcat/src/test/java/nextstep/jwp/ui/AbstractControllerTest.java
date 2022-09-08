package nextstep.jwp.ui;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;
import nextstep.jwp.exception.ResourceNotFoundException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeaders;
import org.apache.coyote.http11.request.StartLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.Test;

class AbstractControllerTest {

    @Test
    void 없는_api_method_요청일_경우_예외를_반환한다() {
        // given
        StartLine startLine = new StartLine("PUT / HTTP/1.1 ");
        RequestHeaders requestHeaders = RequestHeaders.of(List.of("Content-Length: 10"));
        RequestBody requestBody = RequestBody.of("name=eden&nickName=king");
        HttpRequest httpRequest = new HttpRequest(startLine, requestHeaders, requestBody);

        Controller controller = new HomeController();

        // when & then
        assertThatThrownBy(() -> controller.service(httpRequest, new HttpResponse()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

}
