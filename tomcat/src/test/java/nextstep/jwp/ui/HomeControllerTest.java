package nextstep.jwp.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeaders;
import org.apache.coyote.http11.request.StartLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HomeControllerTest {

    @Test
    void home_요청일_경우_hello_world를_반환한다() {
        // given
        StartLine startLine = new StartLine("GET / HTTP/1.1 ");
        RequestHeaders requestHeaders = RequestHeaders.of(List.of("Content-Length: 10"));
        RequestBody requestBody = RequestBody.of("name=eden&nickName=king");
        HttpRequest request = new HttpRequest(startLine, requestHeaders, requestBody);

        HomeController homeController = new HomeController();
        HttpResponse response = new HttpResponse();

        // when
        homeController.doGet(request, response);

        // then
        assertThat(response.asString()).contains("Hello world!");
    }

}
