package org.apache.coyote.http11;

import static com.techcourse.db.InMemoryUserRepository.findByAccount;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

public class HttpParser {

    private static final String HTML = "html";
    private static final String CSS = "css";

    public static Request getRequest(InputStream inputStream) throws IOException {
        Headers headers = Headers.of(inputStream);
        Request request = Request.from(headers.getHeaders());

        if (uri.length > 1 && path.equals("/login")) {
            String[] queryParams = uri[1].split("&");
            for (String param : queryParams) {
                String name = param.split("=")[0];
                String value = param.split("=")[1];
                if (name.equals("account")) {
                    log.info(findByAccount(value).toString());
                    final URL resource = getClass().getClassLoader().getResource("static" + path + "." + html);
                    final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
                    response(responseBody, outputStream, html);
                    return;
                }
            }
        }

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

    }

    public Request parseRequest() throws IOException {

    }
}
