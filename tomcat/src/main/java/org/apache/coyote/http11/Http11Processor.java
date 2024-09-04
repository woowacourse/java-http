package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final String requestMethodAndUrl = bufferedReader.readLine();

            final String[] texts = requestMethodAndUrl.split(" ");
            final var method = texts[0];
            final var path = texts[1];
            log.info("{} 요청 = {}", method, requestMethodAndUrl);
            final Request request = new Request(path);
            log.info("request = {}", request);
            final URL resource = request.getUrl();
            final var result = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            final Response response = new Response();
            if (request.getQueryString().containsKey("account")) {
                //TODO 여기 리팩터링
                createResponse(request, response, result);
            } else {
                generateOKResponse(response, request, result);
            }
            outputStream.write(response.toHttpResponse().getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void generateOKResponse(final Response response, final Request request, final String result) {
        response.setSc("OK");
        response.setStatusCode(200);
        response.setContentType(request.getContentType());
        response.setContentLength(result.getBytes().length);
        response.setSourceCode(result);
    }

    private void createResponse(final Request request, final Response response, final String result) {
        final var queryString = request.getQueryString();
        final String account = queryString.get("account");
        try {
            response.setStatusCode(302);
            response.setSc("FOUND");
            response.setContentType(request.getContentType());
            response.setContentLength(result.getBytes().length);
            response.setSourceCode(result);
            final User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
            final String password = queryString.get("password");
            if (!user.checkPassword(password)) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
            response.setLocation("index.html");
            log.info("user = {}", user);
        } catch (final IllegalArgumentException e) {
            log.warn(e.getMessage());
            response.setLocation("401.html");
        }
    }
}
