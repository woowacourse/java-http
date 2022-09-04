package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.LoginFailedException;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.ExtensionContentType;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.http11handler.Http11Handler;
import org.apache.coyote.http11.http11handler.Http11HandlerSelector;
import org.apache.coyote.http11.http11request.Http11Request;
import org.apache.coyote.http11.http11request.Http11RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String HTTP_SPEC = "HTTP/1.1";

    private final Socket connection;
    private final Http11RequestHandler http11RequestHandler;
    private final Http11HandlerSelector http11HandlerSelector;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.http11RequestHandler = new Http11RequestHandler();
        this.http11HandlerSelector = new Http11HandlerSelector();
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             final var outputStream = connection.getOutputStream()) {
            Http11Request http11Request = http11RequestHandler.makeRequest(bufferedReader);

            Http11Handler http11Handler = http11HandlerSelector.getHttp11Handler(http11Request.getUri());
            Map<String, String> headerElements = http11Handler.extractElements(http11Request.getUri());

            final var response = makeResponse(http11Request.getUri());

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String extractBody(Http11Request http11Request) {
        return "";
    }

    private Map<String, String> extractHeaderElements(Http11Request http11Request) {
        Map<String, String> headerElements = new HashMap<>();
        headerElements.put("spec", HTTP_SPEC);
        headerElements.put("Content-Type", getContentType(http11Request.getUri()));
        headerElements.put("Content-Length", getContentLength(http11Request.getUri()));
        headerElements.put("Content", getContent(http11Request.getUri()));
        return headerElements;
    }

    private String getContent(String uri) {
        return "";
    }

    private String getContentLength(String uri) {
        return "";
    }

    private String getContentType(String uri) {
        int extensionStartIndex = uri.lastIndexOf(".") + 1;
        String extension = uri.substring(extensionStartIndex);
        return ExtensionContentType.toContentType(extension);
    }

    private String makeResponse(String fileName) throws IOException {
        String contentType = "text/html";
        if (isCss(fileName)) {
            contentType = "text/css";
        }
        if (hasQueryString(fileName)) {
            Map<String, String> queryStrings = getQueryStrings(fileName.split("\\?")[1]);
            User user = InMemoryUserRepository.findByAccount(queryStrings.get("account"))
                    .orElseThrow(LoginFailedException::new);
            if (!user.checkPassword(queryStrings.get("password"))) {
                throw new LoginFailedException();
            }
            System.out.println(user);
            fileName = fileName.split("\\?")[0];
        }

        String responseBody = getResponseBody(fileName);
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private Map<String, String> getQueryStrings(String data) {
        return Arrays.stream(data.split("&"))
                .map(it -> it.split("="))
                .collect(Collectors.toMap(it -> it[0], it -> it[1]));
    }

    private boolean hasQueryString(String fileName) {
        return fileName.contains("?");
    }

    private boolean isCss(String fileName) {
        return fileName.contains("/css/");
    }

    private String getResponseBody(String fileName) throws IOException {
        if (fileName.equals("/") || fileName.isEmpty()) {
            return "Hello world!";
        }
        return getContent2(fileName);
    }

    private String getContent2(String fileName) throws IOException {
        if (!fileName.contains(".")) {
            fileName = fileName + ".html";
        }
        Path path = Path.of(Objects.requireNonNull(getClass().getClassLoader().getResource("static" + fileName))
                .getFile());
        return Files.readString(path);
    }
}
