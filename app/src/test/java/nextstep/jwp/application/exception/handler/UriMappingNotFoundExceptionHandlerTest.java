package nextstep.jwp.application.exception.handler;

import nextstep.jwp.framework.common.HttpStatusCode;
import nextstep.jwp.framework.exception.UriMappingNotFoundException;
import nextstep.jwp.framework.message.response.HttpResponseMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.jwp.testutils.TestHttpResponseUtils.assertHeaderIncludes;
import static nextstep.jwp.testutils.TestHttpResponseUtils.assertStatusCode;

class UriMappingNotFoundExceptionHandlerTest {

    @DisplayName("UriMappingNotFoundException 이 발생하면 /404.html 로 리다이렉트한다.")
    @Test
    void run() {
        UriMappingNotFoundExceptionHandler uriMappingNotFoundExceptionHandler = new UriMappingNotFoundExceptionHandler();
        HttpResponseMessage httpResponseMessage = uriMappingNotFoundExceptionHandler.run(
                new UriMappingNotFoundException("")
        );
        assertStatusCode(httpResponseMessage, HttpStatusCode.FOUND);
        assertHeaderIncludes(httpResponseMessage, "Location", "/404.html");
    }
}
