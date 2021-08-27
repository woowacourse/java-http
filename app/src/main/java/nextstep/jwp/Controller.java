package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class Controller {
    public String index() throws IOException {
        return view("/index.html");
    }

    public String login() throws IOException {
        return view("/login.html");
    }

    public String login(String requestBody) {
        Map<String, String> queryMap = bodyMapper(requestBody);

        User user = InMemoryUserRepository.findByAccount(queryMap.get("account")).orElseThrow(
                IllegalArgumentException::new);
        if (!user.checkPassword(queryMap.get("password"))) {
            return unauthorized();
        }
        return found("/index.html");
    }

    public String register() throws IOException {
        return view("/register.html");
    }

    public String register(String requestBody) throws IOException {
        Map<String, String> queryMap = bodyMapper(requestBody);

        User user = new User(2L, queryMap.get("account"), queryMap.get("password"), queryMap.get("email"));

        InMemoryUserRepository.save(user);
        return redirect("/index.html");
    }

    private Map<String, String> bodyMapper(String requestBody) {
        String[] strings = requestBody.split("&");

        Map<String, String> queryMap = new HashMap<>();

        for (String string : strings) {
            String[] token = string.split("=");
            queryMap.put(token[0], token[1]);
        }
        return queryMap;
    }

    private String view(String uri) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static" + uri);
        final Path path = new File(resource.getPath()).toPath();
        String responseBody = Files.readString(path);

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String redirect(String redirectTo) {
        return String.join("\r\n",
                "HTTP/1.1 301 Found ",
                "Location: " + redirectTo);
    }

    private String found(String redirectTo) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + redirectTo);
    }

    private String unauthorized() {
        return String.join("\r\n",
                "HTTP/1.1 401 Unauthorized ");
    }
}
