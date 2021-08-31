package nextstep.jwp.util;

import nextstep.jwp.service.LoginService;
import nextstep.jwp.service.RegisterService;
import nextstep.jwp.service.Service;

public enum Services {
    LOGIN(new LoginService()),
    REGISTER(new RegisterService());

    private final Service service;

    Services(Service service) {
        this.service = service;
    }

    public Service service() {
        return this.service;
    }
}
