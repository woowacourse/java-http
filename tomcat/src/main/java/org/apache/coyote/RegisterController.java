package org.apache.coyote;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.ErrorMessage;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.Response;

import java.util.Map;

import static org.apache.coyote.util.StringParser.parseQueryParameter;

public class RegisterController extends AbstractController {

    private final StaticRenderer staticRenderer = new StaticRenderer();

    @Override
    protected void doPost(Request request, Response response) throws Exception {
        if (register(parseQueryParameter(request.getBody()))) {
            staticRenderer.redirectToIndexPage(response);
            return;
        }
        throw new IllegalArgumentException(ErrorMessage.INVALID_REGISTER_REQUEST.getMessage());
    }

    private boolean register(Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");
        String email = params.get("email");
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return InMemoryUserRepository.findByAccount(account).isPresent();
    }

    @Override
    protected void doGet(Request request, Response response) throws Exception {
        StaticRenderer staticRenderer = new StaticRenderer();
        response.setHttpStatusCode(HttpStatusCode.OK);
        staticRenderer.renderStaticPage(request, response);
    }
}
