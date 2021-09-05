package nextstep.jwp.application.exception.handler;

import nextstep.jwp.framework.common.HttpStatusCode;
import nextstep.jwp.framework.exception.HtmlNotFoundException;
import nextstep.jwp.framework.message.response.HttpResponseMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.jwp.testutils.TestHttpResponseUtils.assertHeaderIncludes;
import static nextstep.jwp.testutils.TestHttpResponseUtils.assertStatusCode;

class HtmlNotFoundExceptionHandlerTest {

    @DisplayName("HtmlNotFoundException 이 발생하면 /404.html 로 리다이렉트한다.")
    @Test
    void run() {
        HtmlNotFoundExceptionHandler htmlNotFoundExceptionHandler = new HtmlNotFoundExceptionHandler();
        HttpResponseMessage httpResponseMessage = htmlNotFoundExceptionHandler.run(new HtmlNotFoundException(""));
        assertStatusCode(httpResponseMessage, HttpStatusCode.FOUND);
        assertHeaderIncludes(httpResponseMessage, "Location", "/404.html");
    }
}
