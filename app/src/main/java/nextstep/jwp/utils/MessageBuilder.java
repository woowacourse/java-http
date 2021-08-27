package nextstep.jwp.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.User;

public class MessageBuilder {

    public static final String FILE_EXTENSION = ".html";

    private MessageBuilder() {
    }

    public static String build(Request request) throws IOException {
        String requestPath = request.getPath();

        if ("/".equals(requestPath)) {
            final String responseBody = "Hello world!";

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        if (!requestPath.contains(FILE_EXTENSION)) {
            String uri = requestPath;
            int index = uri.indexOf("?");
            if (index == -1) {
                requestPath += FILE_EXTENSION;

                final URL resource = MessageBuilder.class.getClassLoader().getResource("static" + requestPath);
                final Path path = new File(resource.getPath()).toPath();
                final String responseBody = new String(Files.readAllBytes(path));

                return String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            }

            String queryString = uri.substring(index + 1);
            String[] queries = queryString.split("&");
            String account = queries[0].split("=")[1];
            String password = queries[1].split("=")[1];
            Optional<User> dbUser = InMemoryUserRepository.findByAccount(account);

            if (dbUser.isPresent() && dbUser.get().checkPassword(password)) {
                return String.join("\r\n",
                        "HTTP/1.1 302 FOUND ",
                        "Location: /index.html ",
                        "",
                        "");
            }

            return String.join("\r\n",
                    "HTTP/1.1 302 FOUND ",
                    "Location: /401.html ",
                    "",
                    "");
        }

        final URL resource = MessageBuilder.class.getClassLoader().getResource("static" + requestPath);
        final Path path = new File(resource.getPath()).toPath();
        final String responseBody = new String(Files.readAllBytes(path));

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
