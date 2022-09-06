package org.apache.coyote.http11.http11handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.http11.ExtensionContentType;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.dto.ResponseComponent;
import org.apache.coyote.http11.http11handler.impl.DefaultPageHandler;
import org.apache.coyote.http11.http11handler.impl.IndexPageHandler;
import org.apache.coyote.http11.http11handler.impl.LoginPageHandler;
import org.apache.coyote.http11.http11handler.impl.RegisterAccountHandler;
import org.apache.coyote.http11.http11handler.impl.RegisterPageHandler;
import org.apache.coyote.http11.http11handler.impl.ResourceHandler;
import org.apache.coyote.http11.http11request.Http11Request;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Http11HandlerTest {

    @DisplayName("DefaultPageHandler handle 테스트")
    @Test
    void extractElements_DefaultPageHandler() {
        Http11Request http11Request = new Http11Request("get", "/", null);
        Http11Handler http11Handler = new DefaultPageHandler();
        ResponseComponent responseComponent = http11Handler.handle(http11Request);

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
        Http11Request http11Request = new Http11Request("get", "/index.html", null);
        Http11Handler http11Handler = new IndexPageHandler();
        ResponseComponent responseComponent = http11Handler.handle(http11Request);

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
        Http11Request http11Request = new Http11Request("get", "/css/styles.css", null);
        Http11Handler http11Handler = new ResourceHandler();
        ResponseComponent responseComponent = http11Handler.handle(http11Request);

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
        Http11Request http11Request = new Http11Request("get", "/login", null);
        Http11Handler http11Handler = new LoginPageHandler();
        ResponseComponent responseComponent = http11Handler.handle(http11Request);

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
        Http11Request http11Request = new Http11Request("get", "/login?account=gugu&password=password", null);
        Http11Handler http11Handler = new LoginPageHandler();
        ResponseComponent responseComponent = http11Handler.handle(http11Request);

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
        Http11Request http11Request = new Http11Request("get", "/login?account=gugu&password=1", null);
        Http11Handler http11Handler = new LoginPageHandler();
        ResponseComponent responseComponent = http11Handler.handle(http11Request);

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

    @DisplayName("RegisterPageHandler handle 테스트")
    @Test
    void extractElements_RegisterPageHandler() {
        Http11Request http11Request = new Http11Request("get", "/register", null);
        Http11Handler http11Handler = new RegisterPageHandler();
        ResponseComponent responseComponent = http11Handler.handle(http11Request);

        String contentLength = Long.toString(new File(Objects.requireNonNull(
                getClass().getClassLoader().getResource("static/register.html")).getFile()).length());
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        assertAll(
                () -> assertThat(responseComponent.getStatusCode()).isEqualTo(StatusCode.OK),
                () -> assertThat(responseComponent.getContentType()).isEqualTo(ExtensionContentType.HTML.getContentType()),
                () -> assertThat(responseComponent.getContentLength()).isEqualTo(contentLength),
                () -> assertThat(responseComponent.getBody()).isEqualTo(new String(Files.readAllBytes(new File(resource.getFile()).toPath())))
        );
    }

    @DisplayName("RegisterAccountHandler handle 성공 테스트")
    @Test
    void handle_RegisterAccountHandler_Success() {
        Http11Request http11Request = new Http11Request("post", "/register", "account=josh&email=whgusrms96@gmail.com&password=password");
        Http11Handler http11Handler = new RegisterAccountHandler();
        ResponseComponent responseComponent = http11Handler.handle(http11Request);

        String contentLength = Long.toString(new File(Objects.requireNonNull(
                getClass().getClassLoader().getResource("static/index.html")).getFile()).length());
        final URL resource = getClass().getClassLoader().getResource("static/index.html");

        assertAll(
                () -> assertThat(InMemoryUserRepository.findByAccount("josh")).isPresent(),
                () -> assertThat(responseComponent.getStatusCode()).isEqualTo(StatusCode.OK),
                () -> assertThat(responseComponent.getContentType()).isEqualTo(ExtensionContentType.HTML.getContentType()),
                () -> assertThat(responseComponent.getContentLength()).isEqualTo(contentLength),
                () -> assertThat(responseComponent.getBody()).isEqualTo(new String(Files.readAllBytes(new File(resource.getFile()).toPath())))
        );
    }

    @DisplayName("RegisterAccountHandler handle 실패 테스트")
    @Test
    void handle_RegisterAccountHandler_Fail() {
        Http11Request http11Request = new Http11Request("post", "/register", "account=gugu&email=whgusrms96@gmail.com&password=password");
        Http11Handler http11Handler = new RegisterAccountHandler();
        ResponseComponent responseComponent = http11Handler.handle(http11Request);

        String contentLength = Long.toString(new File(Objects.requireNonNull(
                getClass().getClassLoader().getResource("static/register.html")).getFile()).length());
        final URL resource = getClass().getClassLoader().getResource("static/register.html");

        assertAll(
                () -> assertThat(responseComponent.getStatusCode()).isEqualTo(StatusCode.BAD_REQUEST),
                () -> assertThat(responseComponent.getContentType()).isEqualTo(ExtensionContentType.HTML.getContentType()),
                () -> assertThat(responseComponent.getContentLength()).isEqualTo(contentLength),
                () -> assertThat(responseComponent.getBody()).isEqualTo(new String(Files.readAllBytes(new File(resource.getFile()).toPath())))
        );
    }
}
