package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import org.apache.catalina.config.TomcatConfig;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.ExceptionHandler;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.session.SessionGenerator;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.FileException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestReader;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final SessionGenerator sessionGenerator;
    private final ExceptionHandler exceptionHandler;
    private final RequestMapping requestMapping;

    public Http11Processor(
            Socket connection,
            SessionGenerator sessionGenerator,
            ExceptionHandler exceptionHandler,
            RequestMapping requestMapping
    ) {
        this.connection = connection;
        this.sessionGenerator = sessionGenerator;
        this.exceptionHandler = exceptionHandler;
        this.requestMapping = requestMapping;
    }

    public static Http11Processor of(Socket connection, TomcatConfig tomcatConfig) {
        return new Http11Processor(
                connection,
                tomcatConfig.sessionGenerator(),
                tomcatConfig.exceptionHandler(),
                tomcatConfig.requestMapping()
        );
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            HttpRequestReader httpRequestReader = new HttpRequestReader(bufferedReader, sessionGenerator);
            HttpRequest request = httpRequestReader.read();
            HttpResponse response = getResponse(request);
            String formattedResponse = response.toResponse();

            outputStream.write(formattedResponse.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse getResponse(HttpRequest request) throws IOException {
        try {
            HttpResponse response = new HttpResponse();
            Controller controller = requestMapping.getController(request);
            controller.service(request, response);

            return response;
        } catch (FileException fileException) {
            return HttpResponse.createRedirectResponse(HttpStatus.FOUND, "/404.html");
        } catch (Exception e) {
            return exceptionHandler.handle(e);
        }
    }
}
