package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private String location = "";

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
             final var outputStream = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            var responseBody = "Hello world!";
            String contentType = "text/html";
            String path = "/";

            String[] firstLine = bufferedReader.readLine().split(" ");
            String method = firstLine[0];
            String url = firstLine[1];
            String version = firstLine[2];

            Map<String, String> requestHeaders = new HashMap<>();
            String line;
            while(!(line = bufferedReader.readLine()).isEmpty()) {
                requestHeaders.put(line.split(": ")[0], line.split(": ")[1]);
            }

            if(method.equals("GET")) {
                path = getPathFromUrl(url);

                if(!path.equals("/")) {
                    String extension = path.split("\\.")[1];
                    if (extension.equals("css")) {
                        contentType = "text/css";
                    }
                }
            }

            if(method.equals("POST")) {
                int contentLength = Integer.parseInt(requestHeaders.get("Content-Length"));
                char[] buffer = new char[contentLength];
                bufferedReader.read(buffer, 0, contentLength);
                String[] requestBody = new String(buffer).split("&");
                Map<String, String> userInfo = new HashMap<>();
                for(String param : requestBody) {
                    userInfo.put(param.split("=")[0], param.split("=")[1]);
                }
                User user = new User(userInfo.get("account"), userInfo.get("password"), userInfo.get("email"));
                InMemoryUserRepository.save(user);
                httpStatusCode = HttpStatusCode.CREATED;
                path = "/index.html";
            }

            responseBody = updateResponseBody(path, responseBody);

            String responseHeader = String.join("\r\n",
                    "HTTP/1.1 " + httpStatusCode.getCode() + " " + httpStatusCode.getName() + " ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ");

            if(!location.isBlank()) {
                responseHeader = String.join("\r\n",
                        responseHeader,
                        location);
            }

            final var response = String.join("\r\n",
                    responseHeader,
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
        httpStatusCode = HttpStatusCode.OK;
        if (url.startsWith("/login")) {
            path = separateQueryString(url, path);
        }
        if (url.startsWith("/register")) {
            path = register(path);
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
            location = "Location: /401.html ";
            return "/401";
        }
        httpStatusCode = HttpStatusCode.FOUND;
        location = "Location: /index.html ";
        log.info(user.toString());
        return "/index";
    }

    private String register(String path) {

        return "/register.html";
    }
}
