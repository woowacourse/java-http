package nextstep.jwp.controller;

import nextstep.jwp.http.controller.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MappingAdvice {

    private static final MappingAdvice instance = new MappingAdvice();
    private static final Map<String, Controller> mappingInfos = new HashMap<>();

    static {
        mappingInfos.put("/", new HelloController());
        mappingInfos.put("/login", new LoginController());
        mappingInfos.put("/register", new RegisterController());
    }

    private MappingAdvice() {
    }

    public static MappingAdvice getInstance() {
        return instance;
    }

    public Optional<Controller> matchController(String uri) {
        return Optional.ofNullable(mappingInfos.get(uri));
    }

}
