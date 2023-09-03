package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.handler.RootPageRequestHandler;
import nextstep.jwp.handler.StaticResourceRequestHandler;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.StartLine;
import org.apache.coyote.http11.request.URI;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseHeaders;
import org.apache.coyote.http11.response.StatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final HttpRequestParser parser = new HttpRequestParser();
    private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

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
        try (final var bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final var bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {

            HttpRequest request = parser.parse(bufferedReader);
            StartLine startLine = request.startLine();
            if (startLine.isEmpty()) {
                return;
            }
            String responseBody = "";
            URI uri = startLine.uri();
            if (uri.path().equals("/")) {
                RootPageRequestHandler rootPageRequestHandler = new RootPageRequestHandler();
                HttpResponse response = rootPageRequestHandler.handle(request);
                bufferedWriter.write(response.toString());
                bufferedWriter.flush();
                return;
            } else if (uri.path().equals("/login")) {
                final URL resource = classLoader.getResource("static" + uri.path() + ".html");
                final File file = new File(resource.getFile());
                responseBody = new String(Files.readAllBytes(file.toPath()));
                User user = InMemoryUserRepository.findByAccount(uri.queryStrings().get("account"))
                        .orElse(null);
                log.info("User={}", user);
            } else {
                StaticResourceRequestHandler staticResourceRequestHandler = new StaticResourceRequestHandler();
                HttpResponse response = staticResourceRequestHandler.handle(request);
                bufferedWriter.write(response.toString());
                bufferedWriter.flush();
                return;
            }

            StatusLine statusLine = new StatusLine(HttpStatus.OK);
            ResponseHeaders responseHeaders = new ResponseHeaders();
            responseHeaders.put("Content-Type", contentType(uri) + "charset=utf-8");
            HttpResponse response = new HttpResponse(statusLine, responseHeaders, responseBody);

            bufferedWriter.write(response.toString());
            bufferedWriter.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String contentType(URI uri) {
        if (uri.path().endsWith(".css")) {
            return "text/css;";
        }
        return "text/html;";
    }
}
