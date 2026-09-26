package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RegisterController extends AbstractController {

    private static final String LOGIN_SUCCESS = "/index.html";
    private final StaticResourceController staticResourceController = new StaticResourceController();

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        staticResourceController.service(request, response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final Map<String, String> formData = parseFormData(request.getBody());
        final User user = new User(formData.get("account"), formData.get("password"), formData.get("email"));
        InMemoryUserRepository.save(user);

        final String setCookie = createSetCookieHeader(request.getCookie(JSESSIONID));
        setRedirectResponse(response, LOGIN_SUCCESS, setCookie);
    }

    private Map<String, String> parseFormData(final String requestBody) {
        final Map<String, String> formData = new HashMap<>();
        final String[] formFields = requestBody.split("&");

        for (String formField : formFields) {
            final String[] keyValue = formField.split("=", 2);
            if (keyValue.length < 2) {
                continue;
            }
            formData.put(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
        }
        return formData;
    }
}
