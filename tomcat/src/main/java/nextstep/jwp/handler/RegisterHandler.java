package nextstep.jwp.handler;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public static boolean handle(HttpRequest request) {
        Map<String, String> body = parse(request.getRequestBody());
        String account = body.get("account");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return false;
        }
        User user = new User(account, body.get("password"), body.get("email"));
        InMemoryUserRepository.save(user);

        return true;
    }

    private static Map<String, String> parse(final String requestBody) {
        return Arrays.stream(requestBody.split("&"))
                .map(data -> data.split("="))
                .collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }
}
