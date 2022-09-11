package nextstep.jwp.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.apache.coyote.http11.exception.NotFoundResourceException;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.RequestUri;
import org.apache.coyote.http11.util.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ResourceRequestHandlerTest {

    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private ResourceRequestHandler resourceRequestHandler;

    @BeforeEach
    void setUp() {
        this.httpRequest = Mockito.mock(HttpRequest.class);
        this.httpResponse = Mockito.mock(HttpResponse.class);
        this.resourceRequestHandler = new ResourceRequestHandler();
    }

    @Test
    void 요청URI에_해당하는_리소스가_존재하는경우_ViewName을_반환한다() {
        // given
        given(httpRequest.getRequestUri()).willReturn(RequestUri.from("/index.html"));

        // when
        final String viewName = resourceRequestHandler.handle(httpRequest, httpResponse);

        // then
        assertAll(
                () -> verify(httpResponse).setStatusCode(HttpStatus.OK),
                () -> assertThat(viewName).isEqualTo("/index.html")
        );
    }

    @Test
    void 요청URI에_해당하는_리소스가_없는_경우_예외가_발생한다() {
        // given
        given(httpRequest.getRequestUri()).willReturn(RequestUri.from("invalid"));

        // when, then
        assertThatThrownBy(() -> resourceRequestHandler.handle(httpRequest, httpResponse))
                .isInstanceOf(NotFoundResourceException.class);
    }
}
