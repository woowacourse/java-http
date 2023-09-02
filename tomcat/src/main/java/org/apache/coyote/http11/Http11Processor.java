package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.util.HttpResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            final String firstLine = bufferedReader.readLine();
            if (firstLine == null) {
                return;
            }
            final RequestLine requestLine = RequestLine.from(firstLine);
            final String response = getResponse(requestLine);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(final RequestLine requestLine) throws IOException {
        final String uri = requestLine.getUri();
        final String path = getPath(uri);
        final QueryString queryString = QueryString.from(uri);
        log.info("request url: {}, queryStrings: {}", uri, queryString.getItems());

        if (path.equals("/")) {
            final String responseBody = "Hello world!";
            return HttpResponseUtil.generate(path, responseBody);
        }
        if (path.equals("/login")) {
            final User account = InMemoryUserRepository.findByAccount(queryString.get("account"))
                    .orElseThrow();
            log.info("login: {}", account);
            final URL resource = classLoader.getResource("static" + path + ".html");
            final File file = new File(resource.getFile());
            final String responseBody = new String(Files.readAllBytes(file.toPath()));
            return HttpResponseUtil.generate(path, responseBody);
        }
        final URL resource = classLoader.getResource("static" + path);
        final File file = new File(resource.getFile());
        final String responseBody = new String(Files.readAllBytes(file.toPath()));
        return HttpResponseUtil.generate(path, responseBody);
    }

    private String getPath(final String uri) {
        int queryStringIndex = uri.indexOf("?");
        if (queryStringIndex == -1) {
            return uri;
        }
        return uri.substring(0, queryStringIndex);
    }
}
