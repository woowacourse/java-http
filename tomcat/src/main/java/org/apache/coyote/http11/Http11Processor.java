package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

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

            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            final String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }

            final String method = requestLine.split(" ")[0];
            final String uri = requestLine.split(" ")[1];

            String line;
            int contentLength = 0;
            while(true) {
                line = reader.readLine();
                if(line == null) {
                    return;
                }
                if(line.isEmpty()) {
                    break;
                }

                String[] header = line.split(":", 2);
                if(header[0].equalsIgnoreCase("Content-Length")) {
                    contentLength = Integer.parseInt(header[1].trim());
                }
            }

            char[] bodyBuffer = new char[contentLength];
            int totalRead = 0;

            while (totalRead < contentLength) {
                int readCount = reader.read(
                        bodyBuffer,
                        totalRead,
                        contentLength - totalRead
                );

                if (readCount == -1) {
                    return;
                }

                totalRead += readCount;
            }

            String body = new String(bodyBuffer);

            final int index = uri.indexOf("?");
            String path = uri;
            String queryString = "";

            if (index >= 0) {
                path = uri.substring(0, index);
                queryString = uri.substring(index + 1);
            }

            byte[] responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);

            // 로그인 success/fail
            if ("/login".equals(path) && method.equals("POST")) {
                final String[] parameters = body.split("&");
                String account = parameters[0].split("=")[1];
                String password = parameters[1].split("=")[1];

                if(InMemoryUserRepository.findByAccount(account).filter(user -> user.checkPassword(password)).isPresent()) {
                    final String responseHeader = String.join("\r\n",
                            "HTTP/1.1 302 Found",
                            "Location: /index.html",
                            "Content-Length: 0",
                            "",
                            "");

                    outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                    return;
                }
                final String responseHeader = String.join("\r\n",
                        "HTTP/1.1 401 Unauthorized",
                        "Location: /401.html",
                        "Content-Length: 0",
                        "",
                        "");

                outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            // 회원가입 처리
            if ("/register".equals(path) && method.equals("POST")) {
                String[] parameters = body.split("&");
                String account = parameters[0].split("=")[1];
                String email = URLDecoder.decode(parameters[1].split("=")[1], StandardCharsets.UTF_8);
                String password = parameters[2].split("=")[1];
                InMemoryUserRepository.save(new User(account, password, email));

                final String responseHeader = String.join("\r\n",
                        "HTTP/1.1 302 Found",
                        "Location: /index.html",
                        "Content-Length: 0",
                        "",
                        "");

                outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            // login 페이지 반환 및 이외
            if (!path.equals("/")) {
                String resourcePath = path;
                if(path.equals("/login")) {
                    resourcePath = "/login.html";
                }
                if(path.equals("/register")) {
                    resourcePath = "/register.html";
                }
                try (final InputStream resourceStream = getClass()
                        .getClassLoader()
                        .getResourceAsStream("static" + resourcePath)) {

                    if (resourceStream == null) {
                        throw new IllegalArgumentException("리소스를 찾을 수 없습니다: " + "static" + resourcePath);
                    }
                    responseBody = resourceStream.readAllBytes();
                }
            }

            final String contentType = getContentType(path);

            final var responseHeader = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType,
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    "");

            outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8 ";
        }
        if (path.endsWith(".js")) {
            return "application/javascript;charset=utf-8 ";
        }
        if (path.endsWith(".svg")) {
            return "image/svg+xml;charset=utf-8 ";
        }
        return "text/html;charset=utf-8 ";
    }
}
