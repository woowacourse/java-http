package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public enum PostRequestUri {
    LOGIN("/login") {
        @Override
        public String create(String requestBody) throws IOException {
            String[] splitBody = requestBody.split("&");
            String account = splitBody[0].split("=")[1];
            String password = splitBody[1].split("=")[1];
            Optional<User> user = InMemoryUserRepository.findByAccount(account);

            if (user.isEmpty() || !user.get().checkPassword(password)) {
                return PostRequestUri.createResponse("/401.html", "302 Found", "text/html", "/401.html");
            }

            return PostRequestUri.createResponse("/index.html", "302 Found", "text/html", "/index.html");
        }
    },
    REGISTER("/register") {
        @Override
        public String create(String requestBody) throws IOException {
            String[] splitBody = requestBody.split("&");
            String account = splitBody[0].split("=")[1];
            String password = splitBody[1].split("=")[1];
            String email = splitBody[2].split("=")[1];

            User user = new User(2, account, password, email);
            InMemoryUserRepository.save(user);

            return PostRequestUri.createResponse("/index.html", "302 Found", "text/html", "/index.html");
        }
    };

    private final String httpRequestUri;

    PostRequestUri(String httpRequestUri) {
        this.httpRequestUri = httpRequestUri;
    }

    public static String createResponse(String requestUri, String requestBody) throws IOException {
        return Arrays.stream(PostRequestUri.values())
                     .filter(postRequestUri -> postRequestUri.hasSameUri(requestUri))
                     .findFirst()
                     .orElseThrow(IllegalArgumentException::new)
                     .create(requestBody);
    }

    private static String createResponse(String requestUri, String statusCode, String contentType, String location) throws IOException {
        final URL resource = PostRequestUri.class.getClassLoader().getResource("static" + requestUri);
        final Path path = new File(resource.getPath()).toPath();
        final List<String> lines = Files.readAllLines(path);

        String result = lines.stream()
                             .map(String::valueOf)
                             .collect(Collectors.joining());

        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Location: " + location + " ",
                "Content-Length: " + result.getBytes().length + " ",
                "", result);
    }

    private boolean hasSameUri(String requestUri) {
        return httpRequestUri.equals(requestUri);
    }

    public abstract String create(String requestBody) throws IOException;
}
