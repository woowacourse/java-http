package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
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
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            StartLine startLine = StartLine.from(bufferedReader.readLine());
            startLine.changeRequestURL();
            List<String> headerLines = getHeaderLines(bufferedReader);
            QueryParams queryParams = QueryParams.from(startLine.getRequestURL());
            HttpRequest httpRequest = new HttpRequest(startLine, headerLines, queryParams);

            processLogin(queryParams);

            String contentType = checkContentType(startLine.getRequestURL());
            String responseBody = getResponseBody(httpRequest);


            var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> getHeaderLines(BufferedReader bufferedReader) throws IOException {
        List<String> headerLines = new ArrayList<>();
        String line = bufferedReader.readLine();
        while (!line.equals("")) {
            headerLines.add(line);
            line = bufferedReader.readLine();
        }
        return headerLines;
    }

    private void processLogin(QueryParams queryParams) {
        if (queryParams.isLoginPath()) {
            String account = queryParams.getParamValue("account");
            String password = queryParams.getParamValue("password");
            checkUser(account, password);
        }
    }

    private void checkUser(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));
        if (user.checkPassword(password)) {
            log.info(user.toString());
        }
    }

    private String checkContentType(String requestURL) {
        String contentType = "text/html";
        if (requestURL.endsWith(".css")) {
            contentType = "text/css";
        }
        return contentType;
    }

    private String getResponseBody(HttpRequest httpRequest) throws IOException {
        if (httpRequest.isMainRequest()) {
            return "Hello world!";
        }
        if (httpRequest.hasParams()) {
            return "success";
        }
        Path path = Paths.get(this.getClass().getClassLoader().getResource("static" + httpRequest.getRequestURL()).getFile());
        return new String(Files.readAllBytes(path));
    }
}
