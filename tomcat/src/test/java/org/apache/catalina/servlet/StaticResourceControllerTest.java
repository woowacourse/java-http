package org.apache.catalina.servlet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.apache.catalina.resource.StaticResourceFinder;
import org.apache.catalina.servlet.controller.StaticResourceController;
import org.apache.support.Dummy;
import org.apache.tomcat.http.exception.MethodNotSupportException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticResourceControllerTest {

    @Test
    @DisplayName("post 요청 시 예외를 던진다.")
    void throw_exception_when_call_post_request() {
        // given
        final var controller = new StaticResourceController("/index.html");

        // when & then
        assertThatThrownBy(() -> controller.doPost(Dummy.getHttpRequest(), Dummy.getHttpResponse()))
                .isInstanceOf(MethodNotSupportException.class);
    }

    @Test
    @DisplayName("get 요청 해당 홈 화면을 반환한다.")
    void return_home_page() throws Exception {
        // given
        final var homeController = new StaticResourceController("/index.html");
        final var httpRequest = Dummy.getHttpRequest();
        final var httpResponse = Dummy.getHttpResponse();

        // when
        homeController.doGet(httpRequest, httpResponse);

        // then
        assertThat(httpResponse).isEqualTo(StaticResourceFinder.render("index.html"));
    }
}
