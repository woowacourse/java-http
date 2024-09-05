package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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

            // inputstream read
            byte[] bytes = new byte[18000];
            int readByteCount = inputStream.read(bytes);
            String data = new String(bytes, 0, readByteCount, StandardCharsets.UTF_8);

            String uri = extractReferer(data);

            // default page
            if (uri.equals("/")) {
                final var responseBody = "Hello world!";

                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            // static file page
            if (uri.endsWith(".html")) {
                String fileName = uri.substring(1);

                URL resource = getClass().getResource("/static/" + fileName);

                Path pt = null;
                try {
                    pt = Path.of(resource.toURI());
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }

                try (BufferedReader bufferedReader = Files.newBufferedReader(pt)) {
                    List<String> actual = bufferedReader.lines().toList();

                    String collect = actual.stream()
                            .collect(Collectors.joining("\n")) + "\n";

                    final var response = String.join("\r\n",
                            "HTTP/1.1 200 OK ",
                            "Content-Type: text/html;charset=utf-8 ",
                            "Content-Length: " + collect.getBytes().length + " ",
                            "",
                            collect);

                    outputStream.write(response.getBytes());
                    outputStream.flush();

                    return;
                } catch (Exception e) {
                }
            }

            // query string parse
            if (uri.startsWith("/login")) {
                int index = uri.indexOf("?");
                String fileName = uri.substring(1, index) + ".html";
                String queryString = uri.substring(index + 1);

                URL resource = getClass().getResource("/static/" + fileName);

                Path pt = null;
                try {
                    pt = Path.of(resource.toURI());
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }

                try (BufferedReader bufferedReader = Files.newBufferedReader(pt)) {
                    List<String> actual = bufferedReader.lines().toList();

                    String collect = actual.stream()
                            .collect(Collectors.joining("\n")) + "\n";

                    final var response = String.join("\r\n",
                            "HTTP/1.1 200 OK ",
                            "Content-Type: text/html;charset=utf-8 ",
                            "Content-Length: " + collect.getBytes().length + " ",
                            "",
                            collect);

                    outputStream.write(response.getBytes());
                    outputStream.flush();

                } catch (Exception e) {
                }

                int index2 = queryString.indexOf("&");
                String accountValue = queryString.substring("account=".length(), index2);
                String passwordValue = queryString.substring(index2 + 1 + "password=".length());

                InMemoryUserRepository.findByAccount(accountValue)
                        .ifPresent(savedUser -> {
                            log.info("user : {}", savedUser);
                        });
            }
        } catch(IOException |UncheckedServletException e)
    {
        log.error(e.getMessage(), e);
    }
}

private static String extractReferer(String httpRequest) {
    String firstLine = httpRequest.split("\n")[0];
    String[] split = firstLine.split(" ");
    if (split[0].equals("GET")) {
        return split[1];
    }
    throw new IllegalArgumentException("GET 요청만 처리 가능..");
}
}
