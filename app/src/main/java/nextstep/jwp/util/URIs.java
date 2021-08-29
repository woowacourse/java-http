package nextstep.jwp.util;

import java.util.Arrays;
import nextstep.jwp.service.ExtraService;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.service.RegisterService;
import nextstep.jwp.service.Service;

public enum URIs {
    LOGIN("/login", new LoginService()),
    REGISTER("/register", new RegisterService());

    private static final Service extraService = new ExtraService();

    private final String uri;
    private final Service service;

    URIs(String uri, Service service) {
        this.uri = uri;
        this.service = service;
    }

    public static Service matchService(String targetURI) {
        return Arrays.stream(URIs.values())
            .filter(element -> element.isSameURIs(targetURI))
            .findAny()
            .map(element -> element.service)
            .orElse(extraService);
    }

    private boolean isSameURIs(String targetURI) {
        return this.uri.equals(targetURI);
    }

}
