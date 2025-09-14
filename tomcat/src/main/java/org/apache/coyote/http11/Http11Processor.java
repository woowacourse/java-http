package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;

import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.renderer.StaticRenderer;
import org.apache.catalina.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    private Request request;

    private Response response;

    private SessionManager sessionManager;

    public Http11Processor(final Socket connection) {
        sessionManager = new SessionManager();
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            request = new Request(br);
            response = new Response(outputStream);
            response.setProtocolVersion("HTTP/1.1");
            String uri = request.getResourcePath();

            // 로그인 처리
            if(uri.startsWith("/login")){
                LoginController loginController = new LoginController(sessionManager);
                loginController.service(request, response);
                return;
            }

            // 회원 가입 처리
            if (uri.startsWith("/register")) {
                RegisterController registerController = new RegisterController();
                registerController.service(request, response);
                return;
            }

            StaticRenderer staticRenderer = new StaticRenderer();
            response.setHttpStatusCode(HttpStatusCode.OK);
            staticRenderer.renderStaticPage(request, response);

        } catch (IOException | UncheckedServletException | URISyntaxException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
