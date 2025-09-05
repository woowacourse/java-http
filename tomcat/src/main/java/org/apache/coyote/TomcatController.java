package org.apache.coyote;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.RequestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TomcatController {

    private static final Logger log = LoggerFactory.getLogger(TomcatController.class);

    private final Map<RequestMapping, Consumer<RequestData>> requestMappings;

    public TomcatController() {
        this.requestMappings = new HashMap<>();
        requestMappings.put(new RequestMapping("/login", HttpMethod.GET), this::handleLogin);
    }

    public void handleRequest(RequestData requestData) {
        for (var entry : requestMappings.entrySet()) {
            if (entry.getKey().isSupported(requestData)) {
                entry.getValue().accept(requestData);
                return;
            }
        }
    }

    private void handleLogin(RequestData requestData) {
        final String account = requestData.getQueryParameterValue("account");
        final String password = requestData.getQueryParameterValue("password");
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
            HttpMethod httpMethod
    ) {

        public boolean isSupported(RequestData requestData) {
            return requestData.getResource().equals(resource)
                    && requestData.getHttpMethod() == httpMethod;
        }
    }
}
