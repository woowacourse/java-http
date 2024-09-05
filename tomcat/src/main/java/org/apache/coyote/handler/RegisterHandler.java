package org.apache.coyote.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.common.Request;
import org.apache.coyote.common.Response;
import org.apache.coyote.common.StatusCode;

public class RegisterHandler implements Handler {

    @Override
    public Response handle(Request request) {
        String account = request.getParameters().get("account");
        String password = request.getParameters().get("password");
        String email = request.getParameters().get("email");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return new Response(StatusCode.BAD_REQUEST, Map.of(), null);
        }
        InMemoryUserRepository.save(new User(account, password, email));
        return new Response(StatusCode.FOUND, Map.of("Location", "/index.html"), null);
    }
}
