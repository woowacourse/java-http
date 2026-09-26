package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.data.SessionManager.JSESSIONID_COOKIE_NAME;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import org.apache.coyote.http11.data.Request;
import org.apache.coyote.http11.data.Response;
import org.apache.coyote.http11.data.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterRequestHandler implements RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(RegisterRequestHandler.class);

    private final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");


    @Override
    public Response doGet(Request request) {
        return Response.view("/register.html");
    }

    @Override
    public Response doPost(Request request) {
        final String account = request.getBody().get("account");
        final String email = request.getBody().get("email");
        final String password = request.getBody().get("password");

        if (!isValidateData(account, email, password)) {
            return Response.badRequest();
        }

        try {
            final User user = new User(account, password, email);

            InMemoryUserRepository.save(user);
            request.getSession().setAttribute("user", user);

            return Response.view("redirect:/index.html");
        } catch (Exception e) {
            e.printStackTrace();
            return Response.view("redirect:/500.html");
        }
    }

    private boolean isValidateData(
            String account,
            String email,
            String password) {
        return isAccountValid(account) && isEmailValid(email) && isPasswordValid(password);
    }

    private boolean isAccountValid(String account) {
        return !Objects.isNull(account) && account.length() >= 3;
    }

    private boolean isPasswordValid(String password) {
        return !Objects.isNull(password) && password.length() >= 6;
    }

    private boolean isEmailValid(String email) {
        return !Objects.isNull(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public boolean canHandle(Request request) {
        return request.getRequestPoint().getPath().equals("/register");
    }
}
