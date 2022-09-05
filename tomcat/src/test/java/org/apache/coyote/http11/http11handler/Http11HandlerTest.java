package org.apache.coyote.http11.http11handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import org.apache.coyote.http11.ExtensionContentType;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.dto.ResponseComponent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Http11HandlerTest {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @DisplayName("DefaultPageHandler handle 테스트")
    @Test
    void extractElements_DefaultPageHandler() {
        Http11Handler http11Handler = new DefaultPageHandler();
        ResponseComponent responseComponent = http11Handler.handle(log, "/");

        assertAll(
                () -> assertThat(responseComponent.getStatusCode()).isEqualTo(StatusCode.OK),
                () -> assertThat(responseComponent.getContentType()).isEqualTo(ExtensionContentType.HTML.getContentType()),
                () -> assertThat(responseComponent.getContentLength()).isEqualTo(Integer.toString("Hello world!".length())),
                () -> assertThat(responseComponent.getBody()).isEqualTo("Hello world!")
        );
    }

    @DisplayName("IndexPageHandler handle 테스트")
    @Test
    void extractElements_IndexPageHandler() {
        Http11Handler http11Handler = new IndexPageHandler();
        ResponseComponent responseComponent = http11Handler.handle(log, "/index.html");

        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        assertAll(
                () -> assertThat(responseComponent.getStatusCode()).isEqualTo(StatusCode.OK),
                () -> assertThat(responseComponent.getContentType()).isEqualTo(ExtensionContentType.HTML.getContentType()),
                () -> assertThat(responseComponent.getContentLength()).isEqualTo("5564"),
                () -> assertThat(responseComponent.getBody()).isEqualTo(new String(Files.readAllBytes(new File(resource.getFile()).toPath())))
        );
    }

    @DisplayName("ResourceHandle handle 테스트")
    @Test
    void extractElements_ResourceHandle() {
        Http11Handler http11Handler = new ResourceHandler();
        ResponseComponent responseComponent = http11Handler.handle(log, "/css/styles.css");

        String contentLength = Long.toString(new File(Objects.requireNonNull(
                getClass().getClassLoader().getResource("static/css/styles.css")).getFile()).length());
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        assertAll(
                () -> assertThat(responseComponent.getStatusCode()).isEqualTo(StatusCode.OK),
                () -> assertThat(responseComponent.getContentType()).isEqualTo(ExtensionContentType.CSS.getContentType()),
                () -> assertThat(responseComponent.getContentLength()).isEqualTo(contentLength),
                () -> assertThat(responseComponent.getBody()).isEqualTo(new String(Files.readAllBytes(new File(resource.getFile()).toPath())))
        );
    }

    @DisplayName("LoginPageHandle handle 테스트")
    @Test
    void extractElements_LoginPageHandle() {
        Http11Handler http11Handler = new LoginPageHandler();
        ResponseComponent responseComponent = http11Handler.handle(log, "/login");

        String contentLength = Long.toString(new File(Objects.requireNonNull(
                getClass().getClassLoader().getResource("static/login.html")).getFile()).length());
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        assertAll(
                () -> assertThat(responseComponent.getStatusCode()).isEqualTo(StatusCode.OK),
                () -> assertThat(responseComponent.getContentType()).isEqualTo(ExtensionContentType.HTML.getContentType()),
                () -> assertThat(responseComponent.getContentLength()).isEqualTo(contentLength),
                () -> assertThat(responseComponent.getBody()).isEqualTo(new String(Files.readAllBytes(new File(resource.getFile()).toPath())))
        );
    }

    @DisplayName("LoginPageHandle handle queryString success 테스트")
    @Test
    void extractElements_LoginPageHandle_QueryString_Success() {
        Http11Handler http11Handler = new LoginPageHandler();
        ResponseComponent responseComponent = http11Handler.handle(log, "/login?account=gugu&password=password");

        String contentLength = Long.toString(new File(Objects.requireNonNull(
                getClass().getClassLoader().getResource("static/index.html")).getFile()).length());
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        assertAll(
                () -> assertThat(responseComponent.getStatusCode()).isEqualTo(StatusCode.FOUND),
                () -> assertThat(responseComponent.getContentType()).isEqualTo(ExtensionContentType.HTML.getContentType()),
                () -> assertThat(responseComponent.getContentLength()).isEqualTo(contentLength),
                () -> assertThat(responseComponent.getBody()).isEqualTo(new String(Files.readAllBytes(new File(resource.getFile()).toPath())))
        );
    }

    @DisplayName("LoginPageHandle handle queryString fail 테스트")
    @Test
    void extractElements_LoginPageHandle_QueryString_Fail() {
        Http11Handler http11Handler = new LoginPageHandler();
        ResponseComponent responseComponent = http11Handler.handle(log, "/login?account=gugu&password=1");

        String contentLength = Long.toString(new File(Objects.requireNonNull(
                getClass().getClassLoader().getResource("static/401.html")).getFile()).length());
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        assertAll(
                () -> assertThat(responseComponent.getStatusCode()).isEqualTo(StatusCode.UNAUTHORIZED),
                () -> assertThat(responseComponent.getContentType()).isEqualTo(ExtensionContentType.HTML.getContentType()),
                () -> assertThat(responseComponent.getContentLength()).isEqualTo(contentLength),
                () -> assertThat(responseComponent.getBody()).isEqualTo(new String(Files.readAllBytes(new File(resource.getFile()).toPath())))
        );
    }
}
