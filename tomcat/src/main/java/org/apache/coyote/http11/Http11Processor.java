package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import nextstep.jwp.RequestMapping;
import nextstep.jwp.commandcontroller.Controller;
import nextstep.jwp.commandcontroller.ErrorPageController;
import nextstep.jwp.commandcontroller.LoginController;
import nextstep.jwp.commandcontroller.LoginPageController;
import nextstep.jwp.commandcontroller.MainPageController;
import nextstep.jwp.commandcontroller.RegisterController;
import nextstep.jwp.commandcontroller.RegisterPageController;
import nextstep.jwp.commandcontroller.StaticResourceController;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final RequestMapping requestMapping;
    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.requestMapping = registerControllers();
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final var outputStream = connection.getOutputStream()) {
            final HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            final HttpResponse httpResponse = new HttpResponse();
            final Controller controller = requestMapping.getController(httpRequest);
            controller.service(httpRequest, httpResponse);
            outputStream.write(httpResponse.toResponse().getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private RequestMapping registerControllers() {
        final RequestMapping mapper = new RequestMapping(List.of(
                new LoginController(),
                new LoginPageController(),
                new RegisterController(),
                new RegisterPageController(),
                new MainPageController(),
                new StaticResourceController()
        ));
        mapper.setDefaultController(new ErrorPageController());
        return mapper;
    }
}
