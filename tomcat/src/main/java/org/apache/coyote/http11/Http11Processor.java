package org.apache.coyote.http11;

import static com.techcourse.db.InMemoryUserRepository.findByAccount;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StreamTokenizer;
import java.net.URL;
import java.nio.file.Files;
import java.util.StringTokenizer;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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

            String html = "html";
            String css = "css";

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String firstLine = reader.readLine();
            StringTokenizer streamTokenizer = new StringTokenizer(firstLine);
            String method = streamTokenizer.nextToken();
            log.info("method: {}", method);
            String query = streamTokenizer.nextToken();
            String[] uri = query.split("\\?");
            String path = uri[0];
            log.info("path: {}", path);

            if (uri.length > 1 && path.equals("/login")) {
                String[] queryParams = uri[1].split("&");
                for (String param : queryParams) {
                    log.info("param: {}", param);
                    String name =  param.split("=")[0];
                    String value =  param.split("=")[1];
                    if (name.equals("account")) {
                        log.info(findByAccount(value).toString());
                        final URL resource = getClass().getClassLoader().getResource("static" + path + "." + html);
                        final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
                        response(responseBody, outputStream, html);
                        return;
                    }
                }
            }

            log.info("path: {}", path);

            if (path.equals("/")) {
                log.info("path is empty");
                final var responseBody = "Hello world!";
                response(responseBody, outputStream, html);
                return;
            }

            if (path.startsWith("/css")) {
                log.info(css);
                final URL resource = getClass().getClassLoader().getResource("static" + path);
                final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
                response(responseBody, outputStream, css);
            }

            final URL resource = getClass().getClassLoader().getResource("static" + path);
            final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            response(responseBody, outputStream, html);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void response(String responseBody, OutputStream outputStream, String type) throws IOException {
        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + type + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
