package org.apache.catalina.servlet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.apache.coyote.http11.exception.NotFoundServletException;
import org.apache.coyote.http11.http.HttpRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    Servlet servlet;
    HttpRequest httpRequest;

    @BeforeEach
    void setUp() {
        servlet = mock(Servlet.class);
        httpRequest = mock(HttpRequest.class);
    }

    @AfterEach
    void tearDown() {
        final RequestMapping requestMapping = RequestMapping.getInstance();
        requestMapping.deleteServlet("/login");
    }

    @Test
    void 경로를_통해서_서블릿을_찾는다() {
        // given
        given(httpRequest.containUrl("/login"))
                .willReturn(true);

        final RequestMapping requestMapping = RequestMapping.getInstance();
        requestMapping.addServlet("/login", servlet);

        // when, then
        assertThat(requestMapping.getServlet(httpRequest)).isEqualTo(servlet);
    }

    @Test
    void 경로에_대한_서블릿을_찾을수_없으면_예외가_발생한다() {
        // given
        given(httpRequest.containUrl("/login"))
                .willReturn(false);
        final RequestMapping requestMapping = RequestMapping.getInstance();

        // when, then
        assertThatThrownBy(() -> requestMapping.getServlet(httpRequest))
                .isInstanceOf(NotFoundServletException.class);
    }
}
