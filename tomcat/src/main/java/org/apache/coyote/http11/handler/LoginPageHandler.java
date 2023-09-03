package org.apache.coyote.http11.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LoginPageHandler implements HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    public boolean support(HttpRequest httpRequest) {
        return httpRequest.isMethodEqualTo("GET") && httpRequest.isUriEqualTo("/login");
    }

    @Override
    public void handle(HttpRequest httpRequest, OutputStream outputStream) throws IOException {
        final String account = httpRequest.getQueryParameter("account");
        final String password = httpRequest.getQueryParameter("password");

        InMemoryUserRepository.findByAccount(account)
                .ifPresent(user -> {
                    if (user.checkPassword(password)) {
                        log.info("user : {}", user);
                    }
                });

        final var responseBody = new StringBuilder();

        final URL indexPageURL = this.getClass().getClassLoader().getResource("static/login.html");
        final File indexFile;
        try {
            indexFile = new File(indexPageURL.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try (
                final FileInputStream fileInputStream = new FileInputStream(indexFile);
                final BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(fileInputStream, StandardCharsets.UTF_8))
        ) {
            while (bufferedReader.ready()) {
                responseBody
                        .append(bufferedReader.readLine())
                        .append(System.lineSeparator());
            }
        }

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.toString().getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
