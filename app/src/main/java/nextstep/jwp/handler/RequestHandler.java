package nextstep.jwp.handler;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.ErrorController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.handler.request.HttpRequest;
import nextstep.jwp.handler.response.HttpResponse;
import nextstep.jwp.util.File;
import nextstep.jwp.util.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final Map<String, Controller> controllerMap = new HashMap<>();

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
        controllerMap.put("/login", new LoginController());
        controllerMap.put("/register", new RegisterController());
    }

    @Override
    public void run() {
        log.debug("Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest request = HttpRequest.from(reader);

            final String response = handleRequest(request);

            final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(response);
            bufferedWriter.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } catch (Exception e) {
            log.error("unknown", e);
        } finally {
            close();
        }
    }

    public String handleRequest(HttpRequest httpRequest) {
        HttpResponse httpResponse = new HttpResponse(httpRequest);
        try {
            if (httpRequest.isRequestStaticFile()) {
                nextstep.jwp.util.File file = FileReader.readFile(httpRequest.getRequestUrl());
                httpResponse.ok(file);
                return httpResponse.makeHttpMessage();
            }

            Controller controller = controllerMap.getOrDefault(httpRequest.getRequestUrl(), new ErrorController());
            controller.handle(httpRequest, httpResponse);
            return httpResponse.makeHttpMessage();
        } catch (FileNotFoundException fileNotFoundException) {
            File file = FileReader.readErrorFile("/404.html");
            httpResponse.notFound("/404.html", file);
            return httpResponse.makeHttpMessage();
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
