package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            final var requestLine = br.readLine();
            log.info("request line: {}", requestLine);

            if (requestLine == null) {
                return;
            }

            final var requestLineTokens = requestLine.split(" ");
            final var url = requestLineTokens[1];

            if ("/".equals(url)) {
                build200Response(outputStream, "text/html", "Hello world!");
            }
            if ("/index.html".equals(url)) {
                buildHtmlResponse(outputStream, url);
            }
            if ("/css/styles.css".equals(url)) {
                buildStyleSheetResponse(outputStream, url);
            }
            if ("/js/scripts.js".equals(url)) {
                buildScriptResponse(outputStream, url);
            }
            if (url.matches("/assets/.*\\.js")) {
                buildScriptResponse(outputStream, url);
            }
            if (url.equals("/login")) {
                buildHtmlResponse(outputStream, url + ".html");
            }
            if (url.matches("/login\\?account=.*&password=.*")) {
                var index = url.indexOf('?');
                var queryString = url.substring(index + 1);

                login(queryString, outputStream);
            }

            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void login(final String queryString, final OutputStream outputStream) throws IOException {
        Map<String, String> params = parseQueryString(queryString);

        if (InMemoryUserRepository.findByAccount(params.get("account")).isEmpty()) {
            buildHtmlResponse(outputStream, "/401.html");
            return;
        }

        final User user = InMemoryUserRepository.findByAccount(params.get("account")).get();
        if (user.checkPassword(params.get("password"))) {
            build302Response(outputStream, "/index.html");
            log.info("user: {}", user);
        }
    }

    private Map<String, String> parseQueryString(final String queryString) {
        final var params = queryString.split("&");

        return Arrays.stream(params)
                .map(param -> param.split("="))
                .collect(Collectors.toMap(token -> token[0], token -> token[1]));
    }

    private String buildResponseBodyFromStaticFile(String fileName) throws IOException {
        final var resourceName = "static" + fileName;
        final var path = Path.of(this.getClass().getClassLoader().getResource(resourceName).getPath());

        return String.join("\n", Files.readAllLines(path)) + "\n";
    }

    private void buildHtmlResponse(final OutputStream outputStream, final String fileName) throws IOException {
        final var responseBody = buildResponseBodyFromStaticFile(fileName);

        build200Response(outputStream, "text/html", responseBody);
    }

    private void buildStyleSheetResponse(final OutputStream outputStream, final String fileName) throws IOException {
        final var responseBody = buildResponseBodyFromStaticFile(fileName);

        build200Response(outputStream, "text/css", responseBody);
    }

    private void buildScriptResponse(final OutputStream outputStream, final String fileName) throws IOException {
        final var responseBody = buildResponseBodyFromStaticFile(fileName);

        build200Response(outputStream, "text/javascript", responseBody);
    }

    private void build200Response(final OutputStream outputStream, final String fileType, final String responseBody) {
        try (final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
             final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(bufferedOutputStream))) {
            writer.write("HTTP/1.1 200 OK \r\n");
            writer.write("Content-Type: " + fileType + ";charset=utf-8 \r\n");
            writer.write("Content-Length: " + responseBody.getBytes().length + " \r\n");
            writer.write("\r\n");
            writer.write(responseBody);
            writer.write("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void build302Response(final OutputStream outputStream, final String location) {
        try (final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
             final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(bufferedOutputStream))) {
            writer.write("HTTP/1.1 302 Found \r\n");
            writer.write("Location: " + location + " \r\n");
            writer.write("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
