package org.apache.catalina.servlet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.apache.coyote.http11.message.request.HttpMethod;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HttpServletTest {

    private TestHttpServlet testHttpServlet;

    @Mock
    private HttpRequest httpRequest;

    private static class TestHttpServlet extends HttpServlet {
        boolean doGetCalled = false;
        boolean doPostCalled = false;

        @Override
        protected void doGet(HttpRequest request, HttpResponse response) {
            doGetCalled = true;
        }

        @Override
        protected void doPost(HttpRequest request, HttpResponse response) {
            doPostCalled = true;
        }
    }

    @BeforeEach
    void setUp() {
        testHttpServlet = new TestHttpServlet();
    }


    @Test
    void GET_요청_시_doGet_메소드를_호출한다() {
        // given && when
        when(httpRequest.getMethod()).thenReturn(HttpMethod.GET);
        testHttpServlet.service(httpRequest, null);

        // then
        assertThat(testHttpServlet.doGetCalled).isTrue();
        assertThat(testHttpServlet.doPostCalled).isFalse();
    }

    @Test
    void POST_요청_시_doPost_메소드를_호출한다() {
        // given && when
        when(httpRequest.getMethod()).thenReturn(HttpMethod.POST);
        testHttpServlet.service(httpRequest, null);

        // then
        assertThat(testHttpServlet.doGetCalled).isFalse();
        assertThat(testHttpServlet.doPostCalled).isTrue();
    }

    @Test
    void 지원하지_않는_HTTP_메소드_요청_시_아무것도_호출하지_않는다() {
        // given && when
        when(httpRequest.getMethod()).thenReturn(null);
        testHttpServlet.service(httpRequest, new HttpResponse());

        // then
        assertThat(testHttpServlet.doGetCalled).isFalse();
        assertThat(testHttpServlet.doPostCalled).isFalse();
    }
}
