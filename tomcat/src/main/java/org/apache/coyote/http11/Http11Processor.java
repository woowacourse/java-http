package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ROOT_PATH = "/";
    private static final String LOGIN_PATH = "/login";
    private static final String ROOT_RESPONSE_BODY = "Hello world!";

    private final Socket connection;
    private final RequestHeaderParser requestHeaderParser;
    private final ResponseBuilder responseBuilder;

    public Http11Processor(final Socket connection) {
        this(connection, new RequestHeaderParser());
    }

    public Http11Processor(final Socket connection, RequestHeaderParser requestHeaderParser) {
        this.connection = connection;
        this.requestHeaderParser = requestHeaderParser;
        this.responseBuilder = new ResponseBuilder();
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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            Map<String, String> requestInformations = requestHeaderParser.parse(bufferedReader);

            String path = requestHeaderParser.getRequestPath(requestInformations);

            String response;
            if (path.equals(ROOT_PATH)) {
                response = responseBuilder.build(
                        HttpStatus.OK,
                        requestHeaderParser.getAccept(requestInformations),
                        processRootRequest()
                );
            } else if (path.equals(LOGIN_PATH)) {
                response = processLoginRequest(requestInformations);
            } else {
                response = responseBuilder.build(
                        HttpStatus.OK,
                        requestHeaderParser.getAccept(requestInformations),
                        processOtherRequest(path)
                );
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String processRootRequest() {
        return ROOT_RESPONSE_BODY;
    }

    private String processLoginRequest(Map<String, String> requestHeaderInfos) throws URISyntaxException, IOException {
        Map<String, String> queryParams = requestHeaderParser.getQueryParams(requestHeaderInfos);

        if (queryParams.isEmpty()) {
            return responseBuilder.build(
                    HttpStatus.OK,
                    requestHeaderParser.getAccept(requestHeaderInfos),
                    readStaticResource(LOGIN_PATH + ".html")
            );
        }

        if (isLoginSuccessful(queryParams)) {
            return responseBuilder.buildRedirect(HttpStatus.FOUND, "/index.html");
        }

        return responseBuilder.buildRedirect(HttpStatus.FOUND, "/401.html");
    }

    private String processOtherRequest(String path) throws URISyntaxException, IOException {
        return readStaticResource(path);
    }

    private boolean isLoginSuccessful(Map<String, String> queryParams) {
        String account = queryParams.get("account");
        String password = queryParams.get("password");

        if (account == null || password == null) {
            return false;
        }

        var user = InMemoryUserRepository.findByAccount(account)
                .filter(foundUser -> foundUser.checkPassword(password));

        user.ifPresent(foundUser -> log.info("{}", foundUser));
        return user.isPresent();
    }

    private String readStaticResource(String path) throws URISyntaxException, IOException {
        String fileName = path.replaceFirst(ROOT_PATH, "");

        URL resource = getClass().getClassLoader().getResource("static/" + fileName);
        if (resource == null) {
            return "";
        }

        return Files.readString(Path.of(resource.toURI()));
    }
}
