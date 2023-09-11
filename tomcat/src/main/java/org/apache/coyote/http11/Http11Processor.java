package org.apache.coyote.http11;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.AuthController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    private final AuthController authController = new AuthController();
    private final RegisterController registerController = new RegisterController();
    private final HttpRequestReader requestReader = new HttpRequestReader();

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
             var outputStream = connection.getOutputStream()) {

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            HttpRequest httpRequest = requestReader.readHttpRequest(bufferedReader);
            HttpResponse httpResponse = new HttpResponse(httpRequest.httpVersion());

            if (httpRequest.path().equals("/login") || httpRequest.path().equals("/login.html")) {
                authController.service(httpRequest, httpResponse);
            } else if (httpRequest.path().equals("/register") || httpRequest.path().equals("/register.html")) {
                registerController.service(httpRequest, httpResponse);
            } else {
                httpResponse.setHttpStatus(HttpStatus.OK).setResponseFileName(httpRequest.path());
            }

            httpResponse.setBody(readFile(httpResponse.getResponseFileName()));
            httpResponse.addHeader("Content-Length", String.valueOf(httpResponse.getBody().getBytes().length));
            httpResponse.addHeader("Content-Type", httpRequest.getContentTypeByAcceptHeader());

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            bufferedOutputStream.write(httpResponse.format().getBytes());
            bufferedOutputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readFile(String fileName) {
        String filePath = this.getClass().getClassLoader().getResource("static" + fileName).getPath();
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            return lines.collect(Collectors.joining("\n", "", "\n"));
        } catch (IOException | UncheckedIOException e) {
            return "Hello world!";
        }
    }
}
