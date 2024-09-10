package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.domain.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private HttpStatusCode httpStatusCode;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.httpStatusCode = HttpStatusCode.OK;
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
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            var responseBody = "Hello world!";
            String url = bufferedReader.readLine().split(" ")[1];

            String path = getPathFromUrl(url);
            responseBody = updateResponseBody(path, responseBody);
            String contentType = "text/html";


            if(!path.equals("/")) {
                String extension = path.split("\\.")[1];
                if (extension.equals("css")) {
                    contentType = "text/css";
                }
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 " + httpStatusCode.getCode() + " " + httpStatusCode.getName() + " ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String updateResponseBody(String path, String responseBody) throws IOException {
        if (!path.equals("/")) {
            URL resource = getClass().getClassLoader().getResource("static" + path);
            final Path filePath = new File(resource.getPath()).toPath();
            responseBody = Files.readString(filePath);
        }
        return responseBody;
    }

    private String getPathFromUrl(String url) {
        String path = url;
        if (url.contains("/login")) {
            path = separateQueryString(url, path);
        }
        return path;
    }

    private String separateQueryString(String url, String path) {
        if (url.contains("?")) {
            int index = url.indexOf("?");
            String queryString = url.substring(index + 1);
            path = validateAccount(queryString);
        }
        return path + ".html";
    }

    private String validateAccount(String queryString) {
        String[] params = queryString.split("&");
        String account = params[0].split("=")[1];
        String password = params[1].split("=")[1];
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        if (!user.checkPassword(password)) {
            httpStatusCode = HttpStatusCode.UNAUTHORIZED;
            return "/401";
        }
        httpStatusCode = HttpStatusCode.FOUND;
        log.info(user.toString());
        return "/index";
    }
}
