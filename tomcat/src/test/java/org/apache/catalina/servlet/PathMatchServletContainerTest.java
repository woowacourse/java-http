package org.apache.catalina.servlet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.catalina.TestPathServletContainerConfig;
import org.apache.catalina.exception.NoMatchedControllerException;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PathMatchServletContainerTest {

    private PathMatchServletContainer pathMatchServletContainer = new PathMatchServletContainer(
            TestPathServletContainerConfig.getRequestMapping()
    );

    @Test
    @DisplayName("매핑 되는 경로의 컨트롤러가 존재하면 요청을 처리할 수 있다.")
    void successService() throws IOException {
        //given
        HttpRequest httpRequest = makeRequestFrom("GET /success HTTP/1.1 ");
        HttpResponse httpResponse = new HttpResponse(httpRequest);

        //when & then
        assertThatCode(() -> pathMatchServletContainer.service(httpRequest, httpResponse))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("매핑 되는 경로의 컨트롤러가 존재하지 않으면 예외가 발생한다.")
    void failServiceNotMatchController() throws IOException {
        //given
        HttpRequest httpRequest = makeRequestFrom("GET /fail HTTP/1.1 ");
        HttpResponse httpResponse = new HttpResponse(httpRequest);

        //when & then
        assertThatThrownBy(() -> pathMatchServletContainer.service(httpRequest, httpResponse))
                .isInstanceOf(NoMatchedControllerException.class)
                .hasMessage(httpRequest.getTarget() + " 요청을 처리할 컨트롤러가 존재하지 않습니다.");
    }

    private HttpRequest makeRequestFrom(String requestMessage) throws IOException {
        var bufferedInputStream = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(requestMessage.getBytes()))
        );
        return HttpRequest.readFrom(bufferedInputStream);
    }
}
