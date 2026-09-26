package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterController extends AbstractController {

    private static final String LOGIN_SUCCESS = "/index.html";
    private static final String REGISTER = "/register";
    private static final String REGISTRATION_FAILURE = REGISTER + "?error=registration-failed";
    private static final int MIN_ACCOUNT_LENGTH = 2;
    private static final int MAX_ACCOUNT_LENGTH = 20;
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@.]+(?:\\.[^\\s@.]+)+$");
    private final StaticResourceController staticResourceController = new StaticResourceController();

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        staticResourceController.service(request, response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final Map<String, String> formData = parseFormData(request.getBody());
        final String account = formData.get("account");
        final String password = formData.get("password");
        final String email = formData.get("email");
        if (!isValidAccount(account) || isBlank(password) || !isValidEmail(email)) {
            setRedirectResponse(response, REGISTRATION_FAILURE, "");
            return;
        }

        final User user = new User(account, password, email);
        if (!InMemoryUserRepository.saveIfAbsent(user)) {
            setRedirectResponse(response, REGISTRATION_FAILURE, "");
            return;
        }

        final String setCookie = createSetCookieHeader(request.getCookie(JSESSIONID));
        setRedirectResponse(response, LOGIN_SUCCESS, setCookie);
    }

    private boolean isValidAccount(final String account) {
        return account != null && !account.isBlank()
                && account.length() >= MIN_ACCOUNT_LENGTH
                && account.length() <= MAX_ACCOUNT_LENGTH;
    }

    private boolean isBlank(final String value) {
        return value == null || value.isBlank();
    }

    private boolean isValidEmail(final String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    private Map<String, String> parseFormData(final String requestBody) {
        final Map<String, String> formData = new HashMap<>();
        final String[] formFields = requestBody.split("&");

        for (String formField : formFields) {
            addFormField(formData, formField);
        }
        return formData;
    }

    private void addFormField(final Map<String, String> formData, final String formField) {
        final String[] keyValue = formField.split("=", 2);
        if (keyValue.length < 2) {
            return;
        }
        formData.put(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
    }
}
