package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;

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
            var line = " ";

            final var httpRequestHeaders = new HashMap<String, String>();
            while (!line.isEmpty()) {
                line = bufferedReader.readLine();
                if (line == null || line.isBlank()) {
                    break;
                }
                final String[] split = line.split(":");
                httpRequestHeaders.put(split[0], split[1]);
            }
            final var method = texts[0];
            final var path = texts[1];
            log.info("{} 요청 = {}", method, requestMethodAndUrl);
            if (method.equals("POST")) {
                final int contentLength = Integer.parseInt(httpRequestHeaders.get("Content-Length").strip());
                final char[] buffer = new char[contentLength];
                bufferedReader.read(buffer, 0, contentLength);
                final String requestBody = new String(buffer);
                log.info("requestBody = {}", requestBody);

                final var body = new HashMap<String, String>();
                final String[] params = requestBody.split("&");
                for (final String param : params) {
                    body.put(param.split("=")[0], param.split("=")[1]);
                }
                final User user = new User(body.get("account"), body.get("password"), body.get("email"));
                InMemoryUserRepository.save(user);

                final Response response = new Response();
                response.setSc("Multiple Choices");
                response.setStatusCode(300);
                response.setLocation("index.html");
                response.setContentType("text/html");
                outputStream.write(response.toHttpResponse().getBytes());
                outputStream.flush();
                return;
            }
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
