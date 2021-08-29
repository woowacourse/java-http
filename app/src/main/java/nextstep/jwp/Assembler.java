package nextstep.jwp;

import nextstep.jwp.adaptor.HandlerAdaptor;
import nextstep.jwp.adaptor.HandlerAdaptorImpl;
import nextstep.jwp.handler.LoginController;
import nextstep.jwp.handler.RegisterController;
import nextstep.jwp.handler.ResourceHandler;
import nextstep.jwp.handler.service.LoginService;
import nextstep.jwp.handler.service.RegisterService;
import nextstep.jwp.mapper.HandlerMapper;
import nextstep.jwp.mapper.HandlerMapperImpl;
import nextstep.jwp.view.ViewResolver;

public class Assembler {

    private final LoginService loginService;
    private final RegisterService registerService;
    private final ResourceHandler resourceHandler;
    private final LoginController loginController;
    private final RegisterController registerController;
    private final HandlerMapper handlerMapper;
    private final HandlerAdaptor handlerAdaptor;
    private final ViewResolver viewResolver;
    private final Dispatcher dispatcher;

    public Assembler() {
        loginService = new LoginService();
        registerService = new RegisterService();
        resourceHandler = new ResourceHandler();
        loginController = new LoginController(loginService);
        registerController = new RegisterController(registerService);
        handlerMapper = new HandlerMapperImpl(loginController, registerController, resourceHandler);
        handlerAdaptor = new HandlerAdaptorImpl();
        viewResolver = new ViewResolver();
        dispatcher = new Dispatcher(handlerMapper, handlerAdaptor, viewResolver);
    }

    public Dispatcher dispatcher() {
        return dispatcher;
    }
}
