package nextstep.jwp;

import nextstep.jwp.context.ApplicationContext;
import nextstep.jwp.context.ApplicationContextImpl;
import nextstep.jwp.dispatcher.adapter.HandlerAdapterFactory;
import nextstep.jwp.dispatcher.mapping.HandlerMappingFactory;
import nextstep.project.presentation.HelloWorldController;
import nextstep.project.presentation.RegisterController;
import nextstep.project.presentation.LoginController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        applicationContext = new ApplicationContextImpl();
        applicationContext.addHandler("/", new HelloWorldController());
        applicationContext.addHandler("/login", new LoginController());
        applicationContext.addHandler("/register", new RegisterController());
    }

    @Test
    void run() {
        // given
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(
            socket,
            new ApplicationContextImpl(),
            HandlerMappingFactory.createAllHandlerMapping(),
            HandlerAdapterFactory.createAllHandlerAdapter()
        );

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(
            socket,
            new ApplicationContextImpl(),
            HandlerMappingFactory.createAllHandlerMapping(),
            HandlerAdapterFactory.createAllHandlerAdapter()
        );

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 5518 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login_with_body() {
        // given
        final String httpRequest = String.join("\r\n",
            "POST /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 30 ",
            "Content-Type: application/x-www-form-urlencoded ",
            "Accept: */* ",
            "",
            "account=gugu&password=password"
            );


        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(
            socket,
            applicationContext,
            HandlerMappingFactory.createAllHandlerMapping(),
            HandlerAdapterFactory.createAllHandlerAdapter()
        );

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n",
            "HTTP/1.1 302 Found ",
            "Location: ./index.html ",
            ""
            );
        assertThat(socket.output())
            .contains("Location: ./index.html", "Set-Cookie: JSESSIONID=");
    }

    @Test
    void login_password_fail() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
            "POST /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 30 ",
            "Content-Type: application/x-www-form-urlencoded ",
            "Accept: */* ",
            "",
            "account=gugu&password=wrong"
        );

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(
            socket,
            applicationContext,
            HandlerMappingFactory.createAllHandlerMapping(),
            HandlerAdapterFactory.createAllHandlerAdapter()
        );

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        String expected = "HTTP/1.1 401 Unauthorized \r\n" +
            "Content-Length: 2426 \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "\r\n"+
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login_not_exists_account_fail() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
            "POST /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 30 ",
            "Content-Type: application/x-www-form-urlencoded ",
            "Accept: */* ",
            "",
            "account=binghe&password=password"
        );

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(
            socket,
            applicationContext,
            HandlerMappingFactory.createAllHandlerMapping(),
            HandlerAdapterFactory.createAllHandlerAdapter()
        );

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        String expected = "HTTP/1.1 401 Unauthorized \r\n" +
            "Content-Length: 2426 \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "\r\n"+
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void register() {
        // given
        final String httpRequest = String.join("\r\n",
            "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com"
            );

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(
            socket,
            applicationContext,
            HandlerMappingFactory.createAllHandlerMapping(),
            HandlerAdapterFactory.createAllHandlerAdapter()
        );

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n",
            "HTTP/1.1 302 Found ",
            "Location: index.html ",
            "",
            ""
        );
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void static_file_css() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
            "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(
            socket,
            applicationContext,
            HandlerMappingFactory.createAllHandlerMapping(),
            HandlerAdapterFactory.createAllHandlerAdapter()
        );

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        String expected = "HTTP/1.1 200 OK \r\n" +
            "Content-Length: 211988 \r\n" +
            "Content-Type: text/css \r\n" +
            "\r\n"+
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }
}
