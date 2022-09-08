package nextstep.jwp.config;

import nextstep.jwp.presentation.DefaultController;
import nextstep.jwp.presentation.GlobalExceptionHandler;
import nextstep.jwp.presentation.LoginController;
import nextstep.jwp.presentation.RegisterController;
import nextstep.jwp.presentation.RootController;
import org.apache.catalina.Configuration;
import org.apache.catalina.ExceptionHandler;
import org.apache.catalina.RequestMapper;

public class NextStepConfig implements Configuration {

    private final RequestMapper requestMapper = new RequestMapper(DefaultController.instance());

    public NextStepConfig() {
        addHandlers();
    }

    @Override
    public void addHandlers() {
        requestMapper.addHandler("/", RootController.instance());
        requestMapper.addHandler("/register", RegisterController.instance());
        requestMapper.addHandler("/login", LoginController.instance());
        requestMapper.addHandler("/login.html", LoginController.instance());
    }

    @Override
    public RequestMapper getRequestMapper() {
        return requestMapper;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return GlobalExceptionHandler.instance();
    }
}
