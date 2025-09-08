package org.apache.catalina.servlet;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ServletContainerTest {

    private final ServletContainer servletContainer = ServletContainer.getInstance();

    private static class TestServlet implements Servlet {
        @Override
        public void service(HttpRequest request, HttpResponse response) {
        }
    }

    @DisplayName("서블릿을 추가하고 경로로 조회할 수 있다.")
    @Test
    void addAndGetServlet() {
        // given
        Servlet testServlet = new TestServlet();
        String path = "/test";
        servletContainer.add(path, testServlet);

        // when
        Servlet retrievedServlet = servletContainer.getServletBy(path);

        // then
        assertThat(retrievedServlet).isSameAs(testServlet);
    }

    @DisplayName("경로에 해당하는 서블릿이 있으면 반환한다.")
    @Test
    void getServletBy_returnsSpecificServlet_whenBothExist() {
        // given
        Servlet specificServlet = new TestServlet();
        String path = "/specific";
        servletContainer.add(path, specificServlet);

        // when
        Servlet retrievedServlet = servletContainer.getServletBy(path);

        // then
        assertThat(retrievedServlet).isEqualTo(specificServlet);
    }

    @DisplayName("등록되지 않은 경로 조회 시 fallback 서블릿을 반환한다.")
    @Test
    void getServletBy_returnsFallback_ifNotFound() {
        // given
        Servlet fallbackServlet = new TestServlet();
        servletContainer.setFallBackServlet(fallbackServlet);

        // when
        Servlet retrievedServlet = servletContainer.getServletBy("/non-existent");

        // then
        assertThat(retrievedServlet).isEqualTo(fallbackServlet);
    }
}
