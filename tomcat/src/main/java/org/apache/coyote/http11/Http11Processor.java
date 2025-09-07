package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader br = new BufferedReader(inputStreamReader);

            final String line = br.readLine();
            final String[] requestHeader = line.split(" ");
            final String httpMethod = requestHeader[0];
            final String endPoint = requestHeader[1];

            if (httpMethod.equals("GET") && endPoint.equals("/")) {
                final String response = createHtmlResponse("Hello world!");
                writeAndFlush(outputStream, response);
                return;
            }

            if (httpMethod.equals("GET") && endPoint.equals("/css/styles.css")) {
                final URL resource = getClass().getClassLoader().getResource("static" + endPoint);
                validateNullResource(resource);
                final String responseBody = Files.readString(Paths.get(resource.toURI()));
                final String response = createCssResponse(responseBody);
                writeAndFlush(outputStream, response);
            }

            if (httpMethod.equals("GET") && endPoint.startsWith("/login")) {
                final int index = endPoint.indexOf("?");
                final String path = endPoint.substring(0, index);

                final String queryString = endPoint.substring(index + 1);
                final String[] splitQueryString = queryString.split("&");
                final String account = splitQueryString[0].split("=")[1];
                final User user = getUserByAccount(account);

                log.info("user: {}", user.toString());
                final URL resource = getClass().getClassLoader().getResource("static" + path + ".html");
                validateNullResource(resource);
                final String responseBody = Files.readString(Paths.get(resource.toURI()));
                final String response = createHtmlResponse(responseBody);
                writeAndFlush(outputStream, response);
                return;
            }

            final URL resource = getClass().getClassLoader().getResource("static" + endPoint);
            validateNullResource(resource);
            final String responseBody = Files.readString(Paths.get(resource.toURI()));
            final String response = createHtmlResponse(responseBody);
            writeAndFlush(outputStream, response);

        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createHtmlResponse(final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String createCssResponse(final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private User getUserByAccount(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("user를 찾을 수 없습니다."));
    }

    private void validateNullResource(final URL resource) {
        if (resource == null) {
            throw new IllegalArgumentException("존재하지 않는 resource 입니다.");
        }
    }

    private void writeAndFlush(final OutputStream outputStream, final String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
