package nextstep.jwp.application.exception.handler;

import nextstep.jwp.framework.common.HttpStatusCode;
import nextstep.jwp.framework.message.response.HttpResponseMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.jwp.testutils.TestHttpResponseUtils.assertStatusCode;

class RuntimeExceptionHandlerTest {

    @DisplayName("따로 핸들링 하지않은 RuntimeException 이 발생하면 500 상태코드의 응답을 만든다.")
    @Test
    void run() {
        RuntimeExceptionHandler runtimeExceptionHandler = new RuntimeExceptionHandler();
        HttpResponseMessage httpResponseMessage = runtimeExceptionHandler.run(new RuntimeException());
        assertStatusCode(httpResponseMessage, HttpStatusCode.INTERNAL_SERVER_ERROR);
    }
}
