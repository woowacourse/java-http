package nextstep.jwp.infrastructure.processor;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.CustomHttpRequest;
import nextstep.jwp.model.User;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Optional;

public class LoginRequestProcessor implements RequestProcessor {

    @Override
    public String processResponse(CustomHttpRequest request, OutputStream outputStream) {
        return "what";
    }
    private String htmlResponse(String fileSource) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + fileSource.getBytes().length + " ",
                "",
                fileSource);
    }

    private void loginProcess(CustomHttpRequest request, OutputStream outputStream) throws IOException {
        Map<String, String> params = request.getParams();
        String account = params.get("account");
        String password = params.get("password");
        Optional<User> byAccount = InMemoryUserRepository.findByAccount(account);
        if (byAccount.isPresent()) {
            if (byAccount.get().checkPassword(password)) {
                final String response = String.join("\r\n",
                        "HTTP/1.1 302 FOUND ",
                        "Location: http://localhost:8080/index.html ",
                        "",
                        "");

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }
            final String response = String.join("\r\n",
                    "HTTP/1.1 302 FOUND ",
                    "Location: http://localhost:8080/401.html ",
                    "",
                    "");

            outputStream.write(response.getBytes());
            outputStream.flush();
        }
    }
}
