package nextstep.jwp;

import nextstep.jwp.handler.LoginController;
import nextstep.jwp.handler.RegisterController;
import nextstep.jwp.handler.ResourceHandler;
import nextstep.jwp.mapper.HandlerMapper;
import nextstep.jwp.mapper.HandlerMapperImpl;
import nextstep.jwp.view.ViewResolver;

public class Assembler {

    private final ResourceHandler resourceHandler;
    private final LoginController loginController;
    private final RegisterController registerController;
    private final HandlerMapper handlerMapper;
    private final ViewResolver viewResolver;
    private final Dispatcher dispatcher;

    public Assembler() {
        resourceHandler = new ResourceHandler();
        loginController = new LoginController();
        registerController = new RegisterController();
        handlerMapper = new HandlerMapperImpl(loginController, registerController, resourceHandler);
        viewResolver = new ViewResolver();
        dispatcher = new Dispatcher(handlerMapper, viewResolver);
    }

    public Dispatcher dispatcher() {
        return dispatcher;
    }
}
