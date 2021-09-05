package nextstep.jwp.application.exception.handler;

import nextstep.jwp.application.exception.UnauthorizedException;
import nextstep.jwp.framework.common.HttpStatusCode;
import nextstep.jwp.framework.message.response.HttpResponseMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.jwp.testutils.TestHttpResponseUtils.assertHeaderIncludes;
import static nextstep.jwp.testutils.TestHttpResponseUtils.assertStatusCode;

class UnauthorizedExceptionHandlerTest {

    @DisplayName("UnauthorizedException 이 발생하면 /401.html 로 리다이렉트한다.")
    @Test
    void run() {
        UnauthorizedExceptionHandler unauthorizedExceptionHandler = new UnauthorizedExceptionHandler();
        HttpResponseMessage httpResponseMessage = unauthorizedExceptionHandler.run(new UnauthorizedException(""));
        assertStatusCode(httpResponseMessage, HttpStatusCode.FOUND);
        assertHeaderIncludes(httpResponseMessage, "Location", "/401.html");
    }
}
