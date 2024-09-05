package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
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

            HttpRequest request = HttpRequestParser.parse(inputStream);

            log.info("request: {}", request);

            // inputstream read
//            byte[] bytes = new byte[18000];
//            int readByteCount = inputStream.read(bytes);
//            String data = new String(bytes, 0, readByteCount, StandardCharsets.UTF_8);
//
//            String uri = extractReferer(data);
            URL resource = getUrl(request);

            if (request.isGetMethod() && resource != null) { // html, js, css 인 경우

                Path path = Path.of(resource.toURI());
                String contentType = Files.probeContentType(path);

                try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
                    List<String> rawBody = bufferedReader.lines().toList();

                    String body = rawBody.stream()
                            .collect(Collectors.joining("\n")) + "\n";

                    final var response = String.join("\r\n",
                            "HTTP/1.1 200 OK ",
                            "Content-Type: " + contentType + ";charset=utf-8 ",
                            "Content-Length: " + body.getBytes().length + " ",
                            "",
                            body);

                    outputStream.write(response.getBytes());
                    outputStream.flush();
                } catch (Exception e) {
                }
            }

            if (request.isGetMethod() && request.getPath().equals("/register")) {
                URL fakeResource = getClass().getResource("/static/register.html");
                Path path = Path.of(fakeResource.toURI());
                String contentType = Files.probeContentType(path);

                try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
                    List<String> rawBody = bufferedReader.lines().toList();

                    String body = rawBody.stream()
                            .collect(Collectors.joining("\n")) + "\n";

                    final var response = String.join("\r\n",
                            "HTTP/1.1 200 OK ",
                            "Content-Type: " + contentType + ";charset=utf-8 ",
                            "Content-Length: " + body.getBytes().length + " ",
                            "",
                            body);

                    outputStream.write(response.getBytes());
                    outputStream.flush();
                } catch (Exception e) {
                }
            }

            if (request.isPostMethod() && request.getPath().equals("/register")) {
                String body = request.getBody();

                String[] bodyParts = body.split("&");
                String account = bodyParts[0].substring("account=".length());
                String email = bodyParts[1].substring("email=".length());
                String password = bodyParts[2].substring("password=".length());

                InMemoryUserRepository.save(new User(account, email, password));

                URL fakeResource = getClass().getResource("/static/index.html");
                Path path = Path.of(fakeResource.toURI());
                String contentType = Files.probeContentType(path);

                try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
                    List<String> rawBody = bufferedReader.lines().toList();

                    String newBody = rawBody.stream()
                            .collect(Collectors.joining("\n")) + "\n";

                    final var response = String.join("\r\n",
                            "HTTP/1.1 302 Found ",
                            "Content-Type: " + contentType + ";charset=utf-8 ",
                            "Content-Length: " + newBody.getBytes().length + " ",
                            "",
                            newBody);

                    outputStream.write(response.getBytes());
                    outputStream.flush();
                } catch (Exception e) {
                }
            }

            // default page
//            if (uri.equals("/")) {
//                final var responseBody = "Hello world!";
//
//                final var response = String.join("\r\n",
//                        "HTTP/1.1 200 OK ",
//                        "Content-Type: text/html;charset=utf-8 ",
//                        "Content-Length: " + responseBody.getBytes().length + " ",
//                        "",
//                        responseBody);
//
//                outputStream.write(response.getBytes());
//                outputStream.flush();
//                return;
//            }
//
//            // static file page
//            if (uri.endsWith(".html")) {
//                String fileName = uri.substring(1);
//
//                URL resource = getClass().getResource("/static/" + fileName);
//
//                Path pt = null;
//                try {
//                    pt = Path.of(resource.toURI());
//                } catch (URISyntaxException e) {
//                    throw new RuntimeException(e);
//                }
//
//                try (BufferedReader bufferedReader = Files.newBufferedReader(pt)) {
//                    List<String> actual = bufferedReader.lines().toList();
//
//                    String collect = actual.stream()
//                            .collect(Collectors.joining("\n")) + "\n";
//
//                    final var response = String.join("\r\n",
//                            "HTTP/1.1 200 OK ",
//                            "Content-Type: text/html;charset=utf-8 ",
//                            "Content-Length: " + collect.getBytes().length + " ",
//                            "",
//                            collect);
//
//                    outputStream.write(response.getBytes());
//                    outputStream.flush();
//
//                    return;
//                } catch (Exception e) {
//                }
//            }
//
//            // query string parse
//            if (uri.startsWith("/login")) {
//                int index = uri.indexOf("?");
//                String fileName = uri.substring(1, index) + ".html";
//                String queryString = uri.substring(index + 1);
//
//                URL resource = getClass().getResource("/static/" + fileName);
//
//                Path pt = null;
//                try {
//                    pt = Path.of(resource.toURI());
//                } catch (URISyntaxException e) {
//                    throw new RuntimeException(e);
//                }
//
//                int index2 = queryString.indexOf("&");
//                String accountValue = queryString.substring("account=".length(), index2);
//                String passwordValue = queryString.substring(index2 + 1 + "password=".length());
//
//                boolean isValidPassword = false;
//                Optional<User> savedUser = InMemoryUserRepository.findByAccount(accountValue);
//                if (savedUser.isPresent()) {
//                    log.info("user : {}", savedUser);
//                    isValidPassword = savedUser.get().checkPassword(passwordValue);
//                }
//
//                try (BufferedReader bufferedReader = Files.newBufferedReader(pt)) {
//                    List<String> actual = bufferedReader.lines().toList();
//
//                    String collect = actual.stream()
//                            .collect(Collectors.joining("\n")) + "\n";
//
////                    final var response = String.join("\r\n",
////                            "HTTP/1.1 200 OK ",
////                            "Content-Type: text/html;charset=utf-8 ",
////                            "Content-Length: " + collect.getBytes().length + " ",
////                            "",
////                            collect);
//
//                    final var response = String.join("\r\n",
//                            "HTTP/1.1 302 Found ",
//                            "Location: http://localhost:8080/" + (isValidPassword ? "index.html" : "401.html"),
//                            "Content-Type: text/html;charset=utf-8 ",
//                            "Content-Length: " + collect.getBytes().length + " ",
//                            "",
//                            collect);
//
//                    outputStream.write(response.getBytes());
//                    outputStream.flush();
//
//                } catch (Exception e) {
//                }
//            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private URL getUrl(HttpRequest request) {
        try {
            return getClass().getResource("/static" + request.getPath());
        } catch (Exception e) {
            return null;
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
