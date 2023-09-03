package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.StringJoiner;

import static org.apache.coyote.http11.ContentType.CSS;
import static org.apache.coyote.http11.ContentType.TEXT_HTML;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String HTTP_11 = "HTTP/1.1";
    private static final String DEFAULT_FILE_ROUTE = "static";

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
             final var outputStream = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            String line = bufferedReader.readLine();
            if (line == null) {
                return;
            }

            HttpResponse httpResponse = createHttpResponse(line);
            String header = createHeader(httpResponse);

            bufferedWriter.write(header + httpResponse.getBody());
            bufferedWriter.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse createHttpResponse(String request) throws IOException {
        String uri = findUri(request);

        int index = uri.indexOf("?");
        String path = uri;
        String query = "";
        if (index != -1) {
            path = uri.substring(0, index);
            query = uri.substring(index + 1);
        }

        return routePath(path, query);
    }

    private String findUri(String request) {
        return Arrays.stream(request.split(" "))
                .filter(it -> it.startsWith("/"))
                .findAny()
                .orElseGet(() -> "");
    }

    private HttpResponse routePath(String path, String query) throws IOException {
        switch (path) {
            case "/":
                return new HttpResponse("Hello world!", HttpStatus.OK, TEXT_HTML);
            case "/login":
                doLogin(query);
                break;
            default:
                break;
        }
        return resolveViewResponse(path);
    }

    private void doLogin(String query) {
        if (query.isBlank()) {
            return;
        }

        String account = "";
        String password = "";

        for (String parameter : query.split("&")) {
            int idx = parameter.indexOf("=");
            String key = parameter.substring(0, idx);
            String value = parameter.substring(idx + 1);
            if ("account".equals(key)) {
                account = value;
            }
            if ("password".equals(key)) {
                password = value;
            }
        }

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
        if (user.checkPassword(password)) {
            System.out.println(user);
        }
    }

    private HttpResponse resolveViewResponse(String path) throws IOException {
        if (!path.contains(".")) {
            path += ".html";
        }

        final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        final URL resource = systemClassLoader.getResource(String.format("%s%s", DEFAULT_FILE_ROUTE, path));

        if (resource == null) {
            final URL notFoundUrl = systemClassLoader.getResource(String.format("%s/%s", DEFAULT_FILE_ROUTE, "404.html"));
            File notFound = new File(notFoundUrl.getPath());
            String body = new String(Files.readAllBytes(notFound.toPath()));
            return new HttpResponse(body, HttpStatus.NOT_FOUND, TEXT_HTML);
        }

        File file = new File(resource.getPath());
        if (file.getName().endsWith(".css")) {
            return new HttpResponse(new String(Files.readAllBytes(file.toPath())), HttpStatus.OK, CSS);
        }
        return new HttpResponse(new String(Files.readAllBytes(file.toPath())), HttpStatus.OK, TEXT_HTML);
    }

    private String createHeader(HttpResponse response) {
        StringJoiner stringJoiner = new StringJoiner("\r\n");
        HttpStatus httpStatus = response.getHttpStatus();

        stringJoiner.add(String.format("%s %d %s ", HTTP_11, httpStatus.getCode(), httpStatus.getMessage()));
        stringJoiner.add(String.format("%s %s ", "Content-Type:", response.getContentType()));
        stringJoiner.add(String.format("%s %s ", "Content-Length:", response.getBody().getBytes().length));
        stringJoiner.add("\r\n");

        return stringJoiner.toString();
    }
}
