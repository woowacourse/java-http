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
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;

public class RegisterController implements Controller {

    @Override
    public boolean isProcessable(HttpRequest httpRequest) {
        return httpRequest.pathEquals("/register");
    }

    @Override
    public Map<String, Object> process(HttpRequest httpRequest) throws URISyntaxException, IOException {
        Map<String, Object> response = new HashMap<>();
        String method = httpRequest.getMethod();

        if (method.equals("POST")) {
            response = postRegister(httpRequest);
        }

        if (method.equals("GET")) {
            response = getRegister(httpRequest);
        }
        return response;
    }

    public Map<String, Object> postRegister(HttpRequest httpRequest) throws IOException, URISyntaxException {
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
            String account = httpRequest.getBodyAttribute("account");
            String password = httpRequest.getBodyAttribute("password");
            String email = httpRequest.getBodyAttribute("email");

            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);

            response.put("responseBody", new String(Files.readAllBytes(path)));
            response.put("status", HttpStatus.FOUND);
        } catch (Exception e) {
            // TODO: 회원가입 실패 시 예외처리
        }

        return response;
    }

    public Map<String, Object> getRegister(HttpRequest httpRequest) throws IOException, URISyntaxException {
        Map<String, Object> response = new HashMap<>();

        final ClassLoader classLoader = getClass().getClassLoader();
        final URL url = classLoader.getResource(httpRequest.getStaticFilePath());

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
