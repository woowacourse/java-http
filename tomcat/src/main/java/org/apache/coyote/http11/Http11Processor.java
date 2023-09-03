package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.sasl.AuthenticationException;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Http11Processor implements Runnable, Processor {

    private static final int URL_INDEX = 1;
    private static final int FIRST_LINE_INDEX = 0;
    private static final int SECOND_LINE_INDEX = 1;
    private static final int START_AFTER_BLANK = 2;

    private static final String SPACE = " ";
    private static final String DELIMITER = ":";
    private static final String STATIC_RESOURCE_DIR = "static";
    private static final String TEXT_HTML = "text/html";
    private static final String TEXT_CSS = "text/css";
    private static final String ACCEPT = "Accept";

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
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            List<String> requestHeader = readRequestHeader(reader);

            Map<String, String> headerPropertyAndValue = parseRequestHeader(requestHeader);

            String requestURI = extractRequestURI(requestHeader);

            String responseBody = makeResponseBody(requestURI);

            String contentType = determineContentType(headerPropertyAndValue);

            sendHttpResponse(outputStream, responseBody, contentType);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> readRequestHeader(BufferedReader reader) throws IOException {
        List<String> requestHeader = new ArrayList<>();

        while (true) {
            String line = reader.readLine();
            if (line == null) {
                throw new IOException("요청에 헤더가 존재하지 않습니다.");
            }

            if (line.isEmpty()) {
                break;
            }

            requestHeader.add(line);
        }

        return requestHeader;
    }

    private Map<String, String> parseRequestHeader(List<String> requestHeader) {
        Map<String, String> headerPropertyAndValue = new HashMap<>();

        for (String line : requestHeader.subList(SECOND_LINE_INDEX, requestHeader.size())) {
            int indexOfDelimiter = line.indexOf(DELIMITER);

            String property = line.substring(0, indexOfDelimiter);
            String value = line.substring(indexOfDelimiter + START_AFTER_BLANK);

            headerPropertyAndValue.put(property, value);
        }
        return headerPropertyAndValue;
    }

    private String extractRequestURI(List<String> requestHeader) throws AuthenticationException {
        String firstLine = requestHeader.get(FIRST_LINE_INDEX);
        String requestURI = firstLine.split(SPACE)[URL_INDEX];

        if (requestURI.startsWith("/login")) {
            int index = requestURI.indexOf("?");
            String queryString = requestURI.substring(index + 1);
            String[] queryParams = queryString.split("&");

            Map<String, String> paramAndValues = new HashMap<>();
            for (String queryParam : queryParams) {
                String[] paramAndValue = queryParam.split("=");
                String param = paramAndValue[0];
                String value = paramAndValue[1];
                paramAndValues.put(param, value);

            }
            Optional<User> optionalUser = InMemoryUserRepository.findByAccount(paramAndValues.get("account"));

            if (optionalUser.isEmpty()) {
                throw new AuthenticationException("회원 정보가 존재하지 않습니다.");
            }

            log.info("user : {}", optionalUser.get());

            requestURI = "/login.html";
        }
        return requestURI;
    }

    private String determineContentType(Map<String, String> headerPropertyAndValue) {
        String contentType = TEXT_HTML;
        if (headerPropertyAndValue.containsKey(ACCEPT) && headerPropertyAndValue.get(ACCEPT).startsWith(TEXT_CSS)) {
            contentType = TEXT_CSS;
        }
        return contentType;
    }

    private void sendHttpResponse(OutputStream outputStream, String responseBody, String contentType) throws IOException {
        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }


    private String makeResponseBody(String requestURL) throws IOException {
        String responseBody = "Hello world!";

        if (!requestURL.equals("/")) {
            ClassLoader classLoader = getClass().getClassLoader();
            final URL resource = classLoader.getResource(STATIC_RESOURCE_DIR + requestURL);
            Path path = new File(resource.getFile()).toPath();

            responseBody = new String(Files.readAllBytes(path));
        }
        return responseBody;
    }
}
