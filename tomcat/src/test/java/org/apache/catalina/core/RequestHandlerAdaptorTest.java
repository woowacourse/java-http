package org.apache.catalina.core;

import static org.apache.catalina.core.HttpServletRequestFixture.createGet;
import static org.apache.catalina.core.RequestHandlerAdaptorFixture.REQUEST_HANDLER_ADAPTOR;
import static org.apache.coyote.http11.common.MimeType.CSS;
import static org.apache.coyote.http11.common.MimeType.HTML;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.catalina.core.servlet.HttpServletResponse;
import org.apache.coyote.http11.common.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RequestHandlerAdaptorTest {


    @DisplayName("/로 GET 요청을 보내면 Hello world!를 반환한다.")
    @Test
    void handleRoot() throws Exception {
        final var httpServletRequest = createGet("/", HTML.toString());
        final var httpServletResponse = new HttpServletResponse();

        REQUEST_HANDLER_ADAPTOR.service(httpServletRequest, httpServletResponse);
        final var response = httpServletResponse.build();

        assertThat(response.getStatus()).isEqualTo(Status.OK);
        assertThat(response.getContentType()).contains(HTML.toString());
        assertThat(response.getBody()).isEqualTo("Hello world!");
    }

    @DisplayName("html 파일명을 자원으로 GET 요청을 보내면 resources/static 디렉토리 내의 동일한 파일을 찾아 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {
            "/index.html", "/login.html", "/register.html", "/401.html"
    })
    void handleHTML(final String URI) throws Exception {
        final var httpServletRequest = createGet(URI, HTML.toString());
        final var httpServletResponse = new HttpServletResponse();

        REQUEST_HANDLER_ADAPTOR.service(httpServletRequest, httpServletResponse);
        final var response = httpServletResponse.build();

        assertThat(response.getStatus()).isEqualTo(Status.OK);
        assertThat(response.getContentType()).isEqualTo(HTML.toString());
    }

    @DisplayName("css 파일명을 자원으로 GET 요청을 보내면 resources/static 디렉토리 내의 동일한 파일을 찾아 반환한다.")
    @Test
    void handleCSS() throws Exception {
        final var httpServletRequest = createGet("/css/styles.css", CSS.toString());
        final var httpServletResponse = new HttpServletResponse();

        REQUEST_HANDLER_ADAPTOR.service(httpServletRequest, httpServletResponse);
        final var response = httpServletResponse.build();

        assertThat(response.getStatus()).isEqualTo(Status.OK);
        assertThat(response.getContentType()).isEqualTo(CSS.toString());
    }

    @DisplayName("resources/static 디렉토리 내에 존재하지 않는 파일명을 자원으로 GET 요청을 보내면 404.html로 리다이렉트한다.")
    @Test
    void handleNotFound() throws Exception {
        final var httpServletRequest = createGet("/neverexist/not.css", CSS.toString());
        final var httpServletResponse = new HttpServletResponse();

        REQUEST_HANDLER_ADAPTOR.service(httpServletRequest, httpServletResponse);
        final var response = httpServletResponse.build();

        assertThat(response.getStatus()).isEqualTo(Status.FOUND);
        assertThat(response.getLocation()).isEqualTo("/404.html");
    }

}
