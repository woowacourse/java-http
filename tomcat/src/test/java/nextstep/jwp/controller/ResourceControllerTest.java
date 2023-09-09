package nextstep.jwp.controller;

import static org.apache.coyote.http11.request.line.HttpMethod.GET;
import static org.apache.coyote.http11.response.line.ResponseStatus.OK;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
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
    void GET_요청일_때_OK로_응답한다() {
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
}
