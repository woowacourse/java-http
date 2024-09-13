package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11RequestBody;
import org.apache.coyote.http11.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final String REGISTER_HTML = "/register.html";
    private static final String REDIRECT_URI = "/register";
    private static final String REQUEST_BODY_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String INDEX_HTML = "/index.html";
    private static final int KEY_VALUE_SIZE = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    @Override
    protected void doGet(Http11Request request, Http11Response response) {
        response.setStaticResponse(request, REGISTER_HTML, 200);
    }

    @Override
    protected void doPost(Http11Request request, Http11Response response) {
        Http11RequestBody requestBody = request.getHttp11RequestBody();
        String body = requestBody.getBody();

        Map<String, String> params = parseBody(body);

        String account = params.get(ACCOUNT);
        String password = params.get(PASSWORD);
        String email = params.get(EMAIL);

        Optional<User> findAccount = InMemoryUserRepository.findByAccount(account);
        if (findAccount.isPresent()) {
            response.setRedirectResponse(REDIRECT_URI, 302, List.of());
            return;
        }
        try {
            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            response.setRedirectResponse(REDIRECT_URI, 302, List.of());
            return;
        }
        response.setRedirectResponse(INDEX_HTML, 302, List.of());
    }

    private Map<String, String> parseBody(String body) {
        return Arrays.stream(body.split(REQUEST_BODY_DELIMITER))
                .map(param -> param.split(KEY_VALUE_DELIMITER))
                .filter(arr -> arr.length == KEY_VALUE_SIZE)
                .collect(Collectors.toMap(arr -> arr[KEY_INDEX], arr -> arr[VALUE_INDEX]));
    }

}
