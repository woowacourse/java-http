package org.apache.catalina.core;

import nextstep.jwp.servlet.DispatcherServlet;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ServletContainerTest {

    @Test
    void URI가_구체적인_서블릿을_먼저_찾는다() {
        // given
        final ServletContainer servletContainer = new ServletContainer();
        servletContainer.registerServlet("/", new DispatcherServlet());
        servletContainer.registerServlet("/mock", new MockServlet());

        // when
        final Servlet servlet = servletContainer.findServlet("/mock")
                .orElseThrow(IllegalArgumentException::new);

        // then
        assertThat(servlet).isInstanceOf(MockServlet.class);
    }
}
