package org.apache.coyote;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TomcatController {

    private static final Logger log = LoggerFactory.getLogger(TomcatController.class);

    private final Map<RequestMapping, Consumer<HttpRequest>> requestMappings;

    public TomcatController() {
        this.requestMappings = new HashMap<>();
        requestMappings.put(new RequestMapping("/login", Method.GET), this::handleLogin);
    }

    public void handleRequest(HttpRequest httpRequest) {
        for (var entry : requestMappings.entrySet()) {
            if (entry.getKey().isSupported(httpRequest)) {
                entry.getValue().accept(httpRequest);
                return;
            }
        }
    }

    private void handleLogin(HttpRequest httpRequest) {
        final String account = httpRequest.getQueryParameterValue("account");
        final String password = httpRequest.getQueryParameterValue("password");
        if (account.isBlank() || password.isBlank()) {
            return;
        }
        final User user = InMemoryUserRepository.findByAccount(account).orElseThrow(() ->
                new IllegalArgumentException("회원이 존재하지 않습니다. : " + account));
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("회원이 존재하지 않습니다. : " + account);
        }
        log.info("회원 조회 성공 : {}", user);
    }

    record RequestMapping(
            String resource,
            Method method
    ) {

        public boolean isSupported(HttpRequest httpRequest) {
            return httpRequest.getPath().equals(resource)
                    && httpRequest.getMethod() == method;
        }
    }
}
