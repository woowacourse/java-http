package nextstep.jwp.application.exception.handler;

import nextstep.jwp.framework.common.HttpStatusCode;
import nextstep.jwp.framework.exception.StaticResourceNotFoundException;
import nextstep.jwp.framework.message.response.HttpResponseMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.jwp.testutils.TestHttpResponseUtils.assertStatusCode;

class StaticResourceNotFoundExceptionHandlerTest {

    @DisplayName("StaticResourceNotFoundException 이 발생하면 404 상태코드의 응답을 만든다.")
    @Test
    void run() {
        StaticResourceNotFoundExceptionHandler staticResourceNotFoundExceptionHandler =
                new StaticResourceNotFoundExceptionHandler();
        HttpResponseMessage httpResponseMessage = staticResourceNotFoundExceptionHandler.run(
                new StaticResourceNotFoundException("")
        );
        assertStatusCode(httpResponseMessage, HttpStatusCode.NOT_FOUND);
    }
}
