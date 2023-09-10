package nextstep.jwp.controller;

import static common.ResponseStatus.OK;
import static org.apache.coyote.request.line.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.line.HttpMethod;
import org.apache.coyote.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ResourceControllerTest {

    @Mock
    private HttpRequest mockHttpRequest;

    @Mock
    private HttpResponse mockHttpResponse;

    private ResourceController resourceController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resourceController = new ResourceController();
    }

    @Test
    void 처리할_수_있는_요청인지_확인한다() {
        // when
        boolean actual = resourceController.canProcess(mockHttpRequest);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void GET_요청일_때_OK로_자원을_응답한다() {
        // given
        when(mockHttpRequest.consistsOf(GET))
                .thenReturn(true);
        when(mockHttpRequest.requestUri())
                .thenReturn("css/styles.css");

        // when
        resourceController.service(mockHttpRequest, mockHttpResponse);

        // then
        verify(mockHttpResponse, times(1)).setResponseResource(OK, "css/styles.css");
    }

    @Test
    void 다른_요청이면_예외를_던진다() {
        // given
        when(mockHttpRequest.consistsOf(any(HttpMethod.class)))
                .thenReturn(false);

        // expect
        assertThatThrownBy(() -> resourceController.service(mockHttpRequest, mockHttpResponse))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
