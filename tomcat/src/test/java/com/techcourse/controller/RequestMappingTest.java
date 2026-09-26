package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.apache.catalina.controller.StaticResourceController;
import org.apache.coyote.http11.HttpRequest;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    private final RequestMapping requestMapping = new RequestMapping();

    @Test
    void 경로에_맞는_컨트롤러를_반환한다() throws IOException {
        assertThat(requestMapping.getController(get("/login"))).isInstanceOf(LoginController.class);
        assertThat(requestMapping.getController(get("/register"))).isInstanceOf(RegisterController.class);
    }

    @Test
    void 매핑되지_않은_경로는_정적_리소스_컨트롤러를_반환한다() throws IOException {
        assertThat(requestMapping.getController(get("/css/styles.css")))
                .isInstanceOf(StaticResourceController.class);
    }

    private HttpRequest get(String path) throws IOException {
        String rawRequest = String.join("\r\n", "GET " + path + " HTTP/1.1", "", "");
        return HttpRequest.from(new BufferedReader(new StringReader(rawRequest)));
    }
}
