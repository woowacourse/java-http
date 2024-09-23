package org.apache.coyote.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.TestFixtures.buildHttpRequest;

class AbstractControllerTest {

    private final RequestMapping requestMapping = RequestMapping.getInstance();

    @CsvSource({"GETT", "DELETE", "ERROR"})
    @ParameterizedTest
    @DisplayName("서버에서 구현되지 않은 메서드를 호출하면 NOT_IMPLEMENTED status 응답을 반환한다.")
    void service_invalidMethod(String method) throws Exception {
        //given
        HttpRequest httpRequest = buildHttpRequest(method, "/", "");

        //when
        HttpResponse httpResponse = requestMapping.dispatch(httpRequest);

        //then
        assertThat(httpResponse.getStatusLine().getHttpStatus()).isEqualTo(HttpStatus.NOT_IMPLEMENTED);
    }

    @Test
    @DisplayName("컨트롤러에서 구현되지 않은 메서드를 호출하면 METHOD_NOT_ALLOWED status 응답을 반환한다.")
    void service_notAllowedMethod() throws Exception {
        //given
        HttpRequest httpRequest = buildHttpRequest("POST", "/fake", "");

        //when
        HttpResponse httpResponse = requestMapping.dispatch(httpRequest);

        //then
        assertThat(httpResponse.getStatusLine().getHttpStatus()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
