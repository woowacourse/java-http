package nextstep;

import nextstep.jwp.controller.Controller;

public class AppConfig {

    private static final AppConfig instance = new AppConfig();

    public final Controller controller;

    private AppConfig() {
        this.controller = controller();
    }

    public static AppConfig getInstance() {
        return instance;
    }

    private Controller controller() {
        return new Controller();
    }
}
