package org.apache.coyote.http11;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
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

            String responseBody = "Hello world!";
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String request = getRequest(bufferedReader);
            String[] split = request.split(" ");
            String method = split[0];
            String requestUri = split[1];



            if (method.equals("POST")) {
                String[] split1 = request.split("\r\n");
                Optional<String> any = Arrays.stream(split1)
                        .filter(it -> it.contains("Content-Length"))
                        .findAny();
                if (any.isEmpty()) {
                    throw new IllegalArgumentException("dfdf");
                }
                String s = any.get();
                int index = s.indexOf(":");
                int contentLength = Integer.parseInt(s.substring(index + 2));
                String body = getBody(bufferedReader, contentLength);

                String[] split2 = body.split("&");
                Map<String, String> form = new HashMap<>();
                for (String line : split2) {
                    String[] split3 = line.split("=");
                    String key = split3[0];
                    String value = split3[1];
                    form.put(key, value);
                }

                if (requestUri.equals("/register")) {
                    register(form, outputStream);
                    return;
                }

                if (requestUri.equals("/login")) {
                    login(form, outputStream);
                    return;
                }
            }

            if (!requestUri.equals("/")) {
                URL resource = getClass().getClassLoader().getResource("static" + requestUri);
                if (requestUri.equals("/login") || requestUri.equals("/register")) {
                    resource = getClass().getClassLoader().getResource("static" + requestUri + ".html");
                }
                responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            }

            String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            if (request.contains("Accept: text/css")) {
                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/css;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void register(Map<String, String> form, OutputStream outputStream) throws IOException {
        Optional<User> account = InMemoryUserRepository.findByAccount(form.get("account"));
        if (account.isPresent()) {
            throw new IllegalStateException("이미 존재하는 아이디입니다. 다른 아이디로 가입해주세요");
        }
        User user = new User(form.get("account"), form.get("password"), form.get("email"));
        System.out.println(user);
        InMemoryUserRepository.save(user);

        String response = String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: /index.html ",
                "",
                "");
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void login(Map<String, String> form, OutputStream outputStream) throws IOException {
        Optional<User> user = InMemoryUserRepository.findByAccount(form.get("account"));
        if (user.isPresent()) {
            if (user.get().checkPassword(form.get("password"))) {
                log.info(user.get().toString());
                String response = String.join("\r\n",
                        "HTTP/1.1 302 FOUND ",
                        "Location: /index.html ",
                        "",
                        "");
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }
        }
        URL resource = getClass().getClassLoader().getResource("static/401.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String response = String.join("\r\n",
                "HTTP/1.1 401 UNAUTHORIZED ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String getRequest(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        StringBuilder stringBuilder = new StringBuilder();
        while (!"".equals(line)) {
            stringBuilder.append(line);
            stringBuilder.append("\r\n");
            line = bufferedReader.readLine();
        }
        return stringBuilder.toString();
    }

    private String getBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        System.out.println(contentLength);
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }
}
