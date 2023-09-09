package nextstep.jwp.controller;

import static org.apache.coyote.http11.request.line.HttpMethod.GET;
import static org.apache.coyote.http11.response.line.ResponseStatus.OK;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
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

    @Test
    void uri가_일치하면_GET_요청을_처리한다() {
        // given
        when(mockHttpRequest.consistsOf(GET, "/"))
                .thenReturn(true);

        // when
        homeController.service(mockHttpRequest, mockHttpResponse);

        // then
        verify(mockHttpRequest, times(1)).consistsOf(GET, "/");
        verify(mockHttpResponse, times(1)).setResponseMessage(OK, "Hello world!");
    }

    @Test
    void uri가_일치하지_않으면_요청을_처리하지_않는다() {
        // given
        when(mockHttpRequest.consistsOf(any(), any()))
                .thenReturn(false);

        // when
        homeController.service(mockHttpRequest, mockHttpResponse);

        // then
        verify(mockHttpRequest, times(1)).consistsOf(any(), any());
        verify(mockHttpResponse, never()).setResponseMessage(OK, "Hello world!");
    }
}
