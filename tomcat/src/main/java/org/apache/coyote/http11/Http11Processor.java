package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class Http11Processor implements Runnable, Processor {
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC = "static";
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final String url = findUrl(inputStream);
            String fileUrl = url;
            HttpStatusCode httpStatusCode = HttpStatusCode.OK;

            final int urlIndex = url.indexOf("?");
            if (urlIndex != -1) {
                final String queryString = url.substring(urlIndex + 1);
                final String replaceQueryString = queryString.replace("account=", "")
                        .replace("password=", "");
                final int delimiterIndex = replaceQueryString.indexOf("&");
                final String account = replaceQueryString.substring(0, delimiterIndex);
                final String password = replaceQueryString.substring(delimiterIndex + 1);
                final User user = InMemoryUserRepository.findByAccount(account)
                        .orElseThrow();
                if (user.checkPassword(password)) {
                    final String userInfo = user.toString();
                    log.info(userInfo);
                    fileUrl = "/index.html";
                    httpStatusCode = HttpStatusCode.FOUND;
                }
                if (!user.checkPassword(password)) {
                    fileUrl = "/401.html";
                }
            }
            if (fileUrl.equals("/login")) {
                fileUrl = url + ".html";
            }

            final String responseBody = makeResponseBody(fileUrl);
            final String contentType = makeContentType(fileUrl);
            final String response = makeResponse(responseBody, contentType, HttpStatusCode.message(httpStatusCode));

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String findUrl(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final String startLine = bufferedReader.readLine();
        return startLine.split(" ")[URI_INDEX];
    }

    private String makeResponseBody(final String url) throws IOException {
        if (url.equals("/")) {
            return "Hello world!";
        }
        final ClassLoader classLoader = getClass().getClassLoader();
        final String filePath = classLoader.getResource(STATIC + url).getPath();
        final String fileContent = new String(Files.readAllBytes(Path.of(filePath)));
        return String.join("\r\n", fileContent);
    }

    private String makeContentType(final String url) {
        if (url.endsWith("css")) {
            return "text/css";
        }
        return "text/html";
    }

    private String makeResponse(final String responseBody, final String contentType, final String httpStatusCode) {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatusCode,
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
