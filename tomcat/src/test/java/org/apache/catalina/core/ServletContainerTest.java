package org.apache.catalina.core;

import nextstep.fixtures.ServletContainerFixtures;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ServletContainerTest {

    @Test
    void URI가_구체적인_서블릿을_먼저_찾는다() {
        // given
        final ServletContainer servletContainer = ServletContainerFixtures.여러가지_URI로_생성();

        // when
        final Servlet servlet = servletContainer.findServlet("/mock")
                .orElseThrow(IllegalArgumentException::new);

        // then
        assertThat(servlet).isInstanceOf(MockServlet.class);
    }
}
