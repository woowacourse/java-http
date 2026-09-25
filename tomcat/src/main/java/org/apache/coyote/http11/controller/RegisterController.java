package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> queryParameters = request.getQueryParameter();
        Optional<User> foundUser = InMemoryUserRepository.findByAccount(queryParameters.get("account"));
        if (foundUser.isPresent()) {
            log.info("회원가입 실패! 아이디 : {}", queryParameters.get("account"));
            response.redirect("/register", "");
            return;
        }
        User user = new User(queryParameters.get("account"), queryParameters.get("password"),
                queryParameters.get("email"));
        InMemoryUserRepository.save(user);
        response.redirect("/index.html", "");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String resource = getStaticResource(request.getPath());
        response.ok(resource, getContentType(request.getPath()));
    }


    private String getContentType(String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css;charset=utf-8 ";
        }
        if (requestUri.endsWith(".js")) {
            return "text/javascript;charset=utf-8 ";
        }
        return "text/html;charset=utf-8 ";
    }

    @Nullable
    private String getStaticResource(String requestUri) throws IOException {
        URL url = getClass().getClassLoader().getResource("static" + requestUri);
        if (url == null) {
            requestUri = requestUri + ".html";
            url = getClass().getClassLoader().getResource("static" + requestUri);
        }
        if (url != null) {
            try (InputStream inputStream = url.openStream()) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
        return null;
    }
}
