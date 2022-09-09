package nextstep.jwp.config;

import nextstep.jwp.presentation.DefaultController;
import nextstep.jwp.presentation.GlobalExceptionHandler;
import nextstep.jwp.presentation.LoginController;
import nextstep.jwp.presentation.RegisterController;
import nextstep.jwp.presentation.RootController;
import org.apache.container.config.Configuration;
import org.apache.container.handler.RequestMapper;

public class NextStepConfig implements Configuration {

    @Override
    public void addHandlers(final RequestMapper requestMapper) {
        requestMapper.addHandler("/", RootController.instance());
        requestMapper.addHandler("/register", RegisterController.instance());
        requestMapper.addHandler("/login", LoginController.instance());
        requestMapper.addHandler("/login.html", LoginController.instance());
    }

    @Override
    public void setDefaultHandler(final RequestMapper requestMapper) {
        requestMapper.setDefaultHandler(DefaultController.instance());
    }

    @Override
    public void setExceptionHandler(final RequestMapper requestMapper) {
        requestMapper.setExceptionHandler(GlobalExceptionHandler.instance());
    }
}
