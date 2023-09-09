package nextstep.jwp.controller;

import static org.apache.coyote.http11.request.line.HttpMethod.GET;
import static org.apache.coyote.http11.response.line.ResponseStatus.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.line.HttpMethod;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HomeControllerTest {

    private HttpRequest mockHttpRequest;
    private HttpResponse mockHttpResponse;
    private HomeController homeController;

    @BeforeEach
    void setUp() {
        mockHttpRequest = Mockito.mock(HttpRequest.class);
        mockHttpResponse = Mockito.mock(HttpResponse.class);
        homeController = new HomeController();
    }

    @ParameterizedTest
    @CsvSource({"true, true", "false, false"})
    void 처리할_수_있는_요청인지_확인한다(boolean returnValue, boolean expected) {
        // given
        String uri = anyString();
        when(mockHttpRequest.consistsOf(uri))
                .thenReturn(returnValue);

        // when
        boolean actual = homeController.canProcess(mockHttpRequest);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void GET_요청이면_OK로_응답한다() {
        // given
        when(mockHttpRequest.consistsOf(GET))
                .thenReturn(true);

        // when
        homeController.service(mockHttpRequest, mockHttpResponse);

        // then
        verify(mockHttpRequest, times(1)).consistsOf(GET);
        verify(mockHttpResponse, times(1)).setResponseMessage(OK, "Hello world!");
    }

    @Test
    void GET_요청이_아니면_예외를_던진다() {
        // given
        when(mockHttpRequest.consistsOf(any(HttpMethod.class)))
                .thenReturn(false);

        // expect
        assertThatThrownBy(() -> homeController.service(mockHttpRequest, mockHttpResponse))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
