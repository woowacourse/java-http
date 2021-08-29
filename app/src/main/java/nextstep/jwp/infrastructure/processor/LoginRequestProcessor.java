package nextstep.jwp.infrastructure.processor;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.CustomHttpRequest;
import nextstep.jwp.model.User;

import java.io.OutputStream;
import java.util.Map;
import java.util.Optional;

public class LoginRequestProcessor implements RequestProcessor {

    @Override
    public String processResponse(CustomHttpRequest request, OutputStream outputStream) {
        Map<String, String> params = request.getParams();
        String account = params.get("account");
        String password = params.get("password");
        Optional<User> byAccount = InMemoryUserRepository.findByAccount(account);

        if (byAccount.isPresent() && byAccount.get().checkPassword(password)) {
            return String.join("\r\n",
                    "HTTP/1.1 302 FOUND ",
                    "Location: http://localhost:8080/index.html ",
                    "",
                    "");
        }
        return String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: http://localhost:8080/401.html ",
                "",
                "");
    }
}
