package org.apache.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.http.HttpStatus;

public class RegisterController implements Controller {

    @Override
    public boolean isProcessable(final String path) {
        return path.contains("/register");
    }

    @Override
    public Map<String, Object> process(final Map<String, String> requests) throws URISyntaxException, IOException {
        Map<String, Object> response = new HashMap<>();
        String method = requests.get("Method");

        if (method.equals("POST")) {
            response = postRegister(requests);
        }

        if (method.equals("GET")) {
            response = getRegister(requests);
        }
        return response;
    }

    public Map<String, Object> postRegister(final Map<String, String> requests) throws IOException, URISyntaxException {
        Map<String, Object> response = new HashMap<>();
        String filePath = "/index.html";
        final ClassLoader classLoader = getClass().getClassLoader();
        final URL url = classLoader.getResource("static" + filePath);

        if (url == null) {
            throw new IOException("파일이 존재하지 않습니다.");
        }

        final File resourceFile = new File(Objects.requireNonNull(url).toURI());
        final Path path = resourceFile.toPath();

        try {
            String account = requests.get("account");
            String password = requests.get("password");
            String email = requests.get("email");

            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);

            response.put("responseBody", new String(Files.readAllBytes(path)));
            response.put("status", HttpStatus.FOUND);
        } catch (Exception e) {
            // TODO: 회원가입 실패 시 예외처리
        }

        return response;
    }

    public Map<String, Object> getRegister(final Map<String, String> requests) throws IOException, URISyntaxException {
        Map<String, Object> response = new HashMap<>();

        String filePath = requests.get("Path") + ".html";
        final ClassLoader classLoader = getClass().getClassLoader();
        final URL url = classLoader.getResource("static" + filePath);

        if (url == null) {
            throw new IOException("파일이 존재하지 않습니다.");
        }

        final File resourceFile = new File(Objects.requireNonNull(url).toURI());
        final Path path = resourceFile.toPath();

        response.put("responseBody", new String(Files.readAllBytes(path)));
        response.put("status", HttpStatus.OK);

        return response;
    }


}
