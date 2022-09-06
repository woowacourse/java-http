package nextstep;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.service.Service;

public class AppConfig {

    private static final AppConfig instance = new AppConfig();

    public final Controller controller;
    public final Service service;

    private AppConfig() {
        this.service = service();
        this.controller = controller(service);
    }

    public static AppConfig getInstance() {
        return instance;
    }

    private Service service() {
        return new Service();
    }

    private Controller controller(final Service service) {
        return new Controller(service);
    }
}
