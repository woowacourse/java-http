package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.service.LoginService;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.util.HttpParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

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
             final var outputStream = connection.getOutputStream()) {
            HttpParser httpParser = new HttpParser(inputStream);
            HttpMethod httpMethod = httpParser.getHttpMethod();
            String httpUrl = httpParser.getHttpUrl();

            if (httpUrl.equals("/favicon.ico")) {
                return;
            }

            String response = "";
            if (httpUrl.equals("/")) {
                String responseBody = "Hello world!";
                response = toResponse(responseBody, "html");
            }

            if (!httpUrl.equals("/") && httpMethod.equals(HttpMethod.GET)) {
                if (httpUrl.contains("login")) {
                    LoginService loginService = new LoginService();
                    loginService.login(httpParser.getQueryParam());
                }
                
                String fileName = ViewResolver.convert(httpUrl);
                File file = getFile(fileName);
                String responseBody = toResponseBody(file);
                FileType fileType = FileType.from(fileName);
                response = toResponse(responseBody, fileType.getType());
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private File getFile(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("static" + fileName);

        return Paths.get(resource.toURI()).toFile();
    }

    private String toResponseBody(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        StringBuilder stringBuilder = new StringBuilder();
        String nextLine = "";
        while ((nextLine = bufferedReader.readLine()) != null) {
            stringBuilder.append(nextLine);
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    private String toResponse(String responseBody, String type) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + type + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
