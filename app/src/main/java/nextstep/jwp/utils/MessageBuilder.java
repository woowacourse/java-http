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

    public static final String FILE_EXTENSION = ".";

    private MessageBuilder() {
    }

    public static String build(Request request) throws IOException {
        String requestPath = request.getPath();
        String requestMethod = request.getMethod();

        if ("GET".equals(requestMethod)) {
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
                    requestPath += ".html";

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

            final String fileType = requestPath.split("\\.")[1];
            final URL resource = MessageBuilder.class.getClassLoader().getResource("static" + requestPath);
            final Path path = new File(resource.getPath()).toPath();
            final String responseBody = new String(Files.readAllBytes(path));

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/" + fileType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        if (requestPath.contains("register")) {
            String requestBody = request.getBody();
            String[] queries = requestBody.split("&");
            String account = queries[0].split("=")[1];
            String email = queries[1].split("=")[1];
            String password = queries[2].split("=")[1];

            if (InMemoryUserRepository.existsByAccount(account)
                    || InMemoryUserRepository.existsByEmail(email)) {
                return String.join("\r\n",
                        "HTTP/1.1 302 FOUND ",
                        "Location: /401.html ",
                        "",
                        "");
            }

            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);

            return String.join("\r\n",
                    "HTTP/1.1 302 FOUND ",
                    "Location: /index.html ",
                    "",
                    "");
        }

        String requestBody = request.getBody();
        String[] queries = requestBody.split("&");
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
}
