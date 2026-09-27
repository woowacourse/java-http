package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;

public class RegisterController extends AbstractController {
    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        ok(response, ResourceLoader.CONTENT_TYPE_HTML, ResourceLoader.loadResponseBody("/register.html"));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        if (!isFormUrlEncoded(request)) {
            unsupportedMediaType(response);
            return;
        }

        Map<String, String> userInfo = request.getFormParameters();
        if (hasBlankParameter(userInfo, "account", "password", "email")) {
            badRequest(response);
            return;
        }

        String account = userInfo.get("account");
        String password = userInfo.get("password");
        String email = userInfo.get("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        found(response, "/index.html");
    }

    private boolean isFormUrlEncoded(HttpRequest request) {
        return request.getHeaders()
                .getOrDefault("Content-Type", "")
                .equals("application/x-www-form-urlencoded");
    }

    private boolean hasBlankParameter(Map<String, String> parameters, String... requiredKeys) {
        for (String key : requiredKeys) {
            String value = parameters.get(key);
            if (value == null || value.isBlank()) {
                return true;
            }
        }

        return false;
    }
}
