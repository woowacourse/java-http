package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.HomePageController;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.StaticResourceController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final Map<String, Controller> controllers = new HashMap<>();
    private static final Controller staticResourceController = StaticResourceController.getInstance();
    private static final Controller homePageController = HomePageController.getInstance();

    static {
        controllers.put("login", LoginController.getInstance());
        controllers.put("register", RegisterController.getInstance());
    }

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            HttpRequest request = new HttpRequest(inputStream);
            HttpResponse response = new HttpResponse(outputStream);

            Controller controller = selectController(request.getPath());
            controller.service(request, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Controller selectController(String path) {
        if ("/".equals(path)) {
            return homePageController;
        }
        if (path.contains(".")) {
            return staticResourceController;
        }
        return controllers.get(path.split("/")[1]);
    }
}
