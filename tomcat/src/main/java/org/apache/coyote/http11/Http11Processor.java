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
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.util.HttpRequestUtil;
import org.apache.coyote.util.HttpResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    private static final int URI_INDEX = 1;

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
            final String request = bufferedReader.readLine();

            if (request == null) {
                return;
            }
            final String uri = request.split(" ")[URI_INDEX];
            final String response = getResponse(uri);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(final String uri) throws IOException {
        final String path = getPath(uri);
        log.info("request url:{}", path);

        if (path.equals("/")) {
            final String responseBody = "Hello world!";
            return HttpResponseUtil.generate(path, responseBody);
        }
        if (path.equals("/login")) {
            final Map<String, String> queryStrings = getQueryString(uri);
            log.info("request url: {}, queryStrings: {}", uri, queryStrings);

            final User account = InMemoryUserRepository.findByAccount(queryStrings.get("account"))
                    .orElseThrow();
            log.info("account login: {}", account);
            
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

    private Map<String, String> getQueryString(final String uri) {
        int queryStringIndex = uri.indexOf("?");
        if (queryStringIndex == -1) {
            return Map.of();
        }
        final String queryString = uri.substring(queryStringIndex + 1);
        return HttpRequestUtil.parseQueryString(queryString);
    }
}
