package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
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
        log.info(
                "connect host: {}, port: {}",
                connection.getInetAddress(),
                connection.getPort()
        );
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            HttpRequest httpRequest = getHttpRequest(inputStream);
            Map<String, String> queryStrings = httpRequest.getQueryStrings();
            String contentType = "text/" + httpRequest.extractExtension();
            String responseBody = "Hello world!";

            if (!httpRequest.path.equals("/")) {
                responseBody = readForFilePath(convertAbsoluteUrl(httpRequest));
            }

            if (httpRequest.path.equals("/login")) {
                String account = queryStrings.get("account");
                String password = queryStrings.get("password");
                User savedUser = InMemoryUserRepository.findByAccount(account)
                        .orElseThrow(() -> new IllegalArgumentException("올바르지 않은 유저입니다."));

                if (savedUser.checkPassword(password)) {
                    log.info("user : {}", savedUser);
                }
            }

            final String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
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

    private HttpRequest getHttpRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder content = new StringBuilder();
        String line;

        do {
            line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            content.append(line).append("\n");
        } while (!"".equals(line));

        return extractHttpRequest(content);
    }

    private HttpRequest extractHttpRequest(final StringBuilder content) {
        String firstLine = content.toString()
                .split("\n")[0];
        String[] methodAndRequestUrl = firstLine.split(" ");

        return HttpRequest.of(methodAndRequestUrl[0], methodAndRequestUrl[1]);
    }

    private String readForFilePath(URL path) {
        try (FileInputStream fileInputStream = new FileInputStream(path.getPath())) {
            return readFile(fileInputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    private String readFile(FileInputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder fileContent = new StringBuilder();
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            fileContent.append(line).append("\n");
        }
        return fileContent.toString();
    }

    private URL convertAbsoluteUrl(HttpRequest httpRequest) {
        return getClass().getClassLoader()
                .getResource("static" + httpRequest.getFilePath());
    }

}
