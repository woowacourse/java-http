package nextstep.jwp.controller;

import static org.apache.coyote.request.line.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import common.ResponseStatus;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class IndexControllerTest {

    @Mock
    private HttpRequest mockHttpRequest;

    @Mock
    private HttpResponse mockHttpResponse;

    @InjectMocks
    private IndexController indexController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @CsvSource({"true, true", "false, false"})
    void 처리할_수_있는_요청인지_확인한다(boolean returnValue, boolean expected) {
        // given
        String uri = anyString();
        when(mockHttpRequest.consistsOf(uri))
                .thenReturn(returnValue);

        // when
        boolean actual = indexController.canProcess(mockHttpRequest);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/index", "/index.html"})
    void GET_요청일_때_리소스를_응답한다(String requestUri) {
        // given
        when(mockHttpRequest.consistsOf(GET))
                .thenReturn(true);
        when(mockHttpRequest.requestUri())
                .thenReturn(requestUri);

        // when
        indexController.service(mockHttpRequest, mockHttpResponse);

        // then
        verify(mockHttpResponse, times(1)).setResponseResource(
                any(ResponseStatus.class), anyString(), anyString()
        );
    }
}
