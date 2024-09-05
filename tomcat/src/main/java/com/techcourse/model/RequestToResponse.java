package com.techcourse.model;

import com.techcourse.db.InMemoryUserRepository;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

public class RequestToResponse {

    public String build(HttpRequest request) throws IOException {
        return responseBuilder(request);
    }

    private String responseBuilder(HttpRequest request) throws IOException {
        final String BASIC_RESPONSE_BODY = "Hello world!";
        final String BASIC_RESPONSE = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + BASIC_RESPONSE_BODY.getBytes().length + " ",
                "",
                BASIC_RESPONSE_BODY);


        if (request.getRequestLine().getPath().equals("/index.html")) {
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "", responseBody
            );
        }

        if (request.getRequestLine().getPath().equals("/css/styles.css")) {
            final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/css;*/*;q=0.1 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "", responseBody
            );
        }

        if (request.getRequestLine().getPath().endsWith(".js")) {
            final URL resource = getClass().getClassLoader().getResource("static" + request.getRequestLine().getPath());
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: application/javascript;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "", responseBody
            );
        }

        if (request.getRequestLine().getPath().startsWith("/login")) {
            return login(request.getRequestLine());
        }

        return BASIC_RESPONSE;
    }

    private String login(RequestLine requestLine) throws IOException {
        String uris = requestLine.getPath();
        int index = uris.indexOf("?");
        String uri = uris.substring(0, index);
        String queryString = uris.substring(index + 1);
        final URL resource = getClass().getClassLoader().getResource("static" + uri + ".html");
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        List<String> infos = List.of(queryString.split("&"));
        List<String> ids = List.of(infos.get(0).split("="));
        List<String> passwords = List.of(infos.get(1).split("="));
        User user = InMemoryUserRepository.findByAccount(ids.get(1)).get();
        if (user.checkPassword(passwords.get(1))) {
            //log.info(user.toString());
            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "", responseBody
            );
        }
        return "";
    }
}
