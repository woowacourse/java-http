package org.apache.coyote.http11;

import static org.apache.coyote.support.Parser.parseQueryString;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.support.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String DEFAULT_VIEW_EXTENSION = ".html";
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String DEFAULT_RESOURCE_PACKAGE = "static";
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
    public void process(Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            String firstLine = new BufferedReader(new InputStreamReader(inputStream)).readLine();
            String response = generateResponse(firstLine);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String generateResponse(final String firstLine) throws IOException {
        String parsedURI = Parser.parseUri(firstLine);
        String responseBody = generateResponseBody(parsedURI);
        String contentType = getContentType(firstLine);

        parseQuery(parsedURI);
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                contentType,
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String getContentType(final String firstLine) {
        if (firstLine.contains("/css")) {
            return "Content-Type: text/css;charset=utf-8 ";
        }
        if (firstLine.contains("/js")) {
            return "Content-Type: text/js;charset=utf-8 ";
        }
        return "Content-Type: text/html;charset=utf-8 ";
    }

    private String generateResponseBody(final String parsedURI) throws IOException {
        String resource = parsedURI.split("\\?")[0];
        if ("/".equals(parsedURI)) {
            return DEFAULT_RESPONSE_BODY;
        }

        if (parsedURI.contains("login")) {
            resource += DEFAULT_VIEW_EXTENSION;
        }

        try {
            Path path = new File(
                    getClass().getClassLoader().getResource(DEFAULT_RESOURCE_PACKAGE + resource).getFile()
            ).toPath();
            return Files.readString(path);
        } catch (NullPointerException e) {
            return DEFAULT_RESPONSE_BODY;
        }
    }

    private void parseQuery(final String parsedURI) {
        if (parsedURI.contains("?")) {
            Map<String, String> queryMap = parseQueryString(parsedURI);
            InMemoryUserRepository.findByAccountAndPassword(queryMap.get("account"), queryMap.get("password"));
        }
    }
}
