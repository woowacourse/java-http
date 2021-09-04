package nextstep.jwp.application.controller;

import nextstep.jwp.framework.common.HttpStatusCode;
import nextstep.jwp.framework.common.MediaType;
import nextstep.jwp.framework.mapper.ControllerMapper;
import nextstep.jwp.framework.message.request.HttpRequestMessage;
import nextstep.jwp.framework.message.response.HttpResponseMessage;
import nextstep.jwp.testutils.TestHttpRequestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static nextstep.jwp.testutils.TestHttpResponseUtils.assertContainsBodyString;
import static nextstep.jwp.testutils.TestHttpResponseUtils.assertHeaderIncludes;
import static nextstep.jwp.testutils.TestHttpResponseUtils.assertStatusCode;

class HelloControllerTest {

    private final ControllerMapper controllerMapper = ControllerMapper.getInstance();
    private final HelloController helloController = (HelloController) controllerMapper.resolve("/");

    @DisplayName("GET 요청의 동작을 확인한다.")
    @Test
    void doGet() throws IOException {
        // given
        String requestMessage = String.join("\r\n",
                "GET / HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");
        HttpRequestMessage httpRequestMessage = TestHttpRequestUtils.makeRequest(requestMessage);

        // when
        HttpResponseMessage httpResponseMessage = helloController.doGet(httpRequestMessage);

        // then
        assertStatusCode(httpResponseMessage, HttpStatusCode.OK);
        assertContainsBodyString(httpResponseMessage, "Hello");
        assertHeaderIncludes(httpResponseMessage, "Content-Type", MediaType.TEXT_HTML_CHARSET_UTF8.getValue());
    }
}
