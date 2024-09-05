package org.apache.coyote.http;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

public class RequestToResponse {

    private static final Logger log = LoggerFactory.getLogger(RequestToResponse.class);

    private static final String BASIC_RESPONSE_BODY = "Hello world!";
    private static final String BASIC_RESPONSE = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + BASIC_RESPONSE_BODY.getBytes().length + " ",
            "",
            BASIC_RESPONSE_BODY);
    private static final String STATIC = "static";
    private static final String INDEX = "/index.html";

    public String build(HttpRequest request) throws IOException {
        String path = request.getRequestLine().getPath();

        if (path.equals("/")) {
            return BASIC_RESPONSE;
        }

        if (path.equals(INDEX)) {
            final URL resource = getClass().getClassLoader().getResource(STATIC.concat(INDEX));
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            StatusLine statusLine = new StatusLine(HttpStatus.OK);
            Header header = Header.of(List.of(
                    "Content-Type: text/html;charset=utf-8",
                    "Content-Length: ".concat(String.valueOf(responseBody.getBytes().length))
            ));

            HttpResponse response = new HttpResponse(statusLine, header, responseBody);
            return response.toResponse();
        }

        if (request.getRequestLine().getPath().equals("/css/styles.css")) {
            final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/css ",
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

        return String.join("\r\n",
                "HTTP/1.1 404 NOT FOUND ",
                "Content-Type: text/plain;charset=utf-8 ",
                "Content-Length: 0 ");
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
            log.info(user.toString());
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
