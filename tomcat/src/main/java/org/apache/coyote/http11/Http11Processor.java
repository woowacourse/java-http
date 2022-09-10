package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.RequestMapping;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final SessionManager SESSION_MANAGER = new SessionManager();
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             final var dataOutputStream = new DataOutputStream(outputStream)) {
            HttpRequest request = new HttpRequest(bufferedReader);
            HttpResponse response = new HttpResponse(dataOutputStream);

            String path = request.getPath();
            Controller controller = RequestMapping.getController(path);
            if (controller == null) {
                response.forward(path);
                return;
            }
            controller.service(request, response);

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SessionManager getSessionManager() {
        return SESSION_MANAGER;
    }
}
