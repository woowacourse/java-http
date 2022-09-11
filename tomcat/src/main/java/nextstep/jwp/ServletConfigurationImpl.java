package nextstep.jwp;

import customservlet.ChicChocServlet;
import customservlet.MappedExceptionResolvers;
import customservlet.MappedRequestHandlers;
import customservlet.RequestHandlerInfo;
import customservlet.ServletConfiguration;
import nextstep.jwp.exception.AuthenticationException;
import customservlet.exception.BadRequestException;
import org.apache.coyote.http11.exception.NotFoundException;
import nextstep.jwp.presentation.controller.LoginPageRequestHandler;
import nextstep.jwp.presentation.controller.LoginRequestHandler;
import nextstep.jwp.presentation.controller.RegisterPageRequestHandler;
import nextstep.jwp.presentation.controller.RegisterRequestHandler;
import nextstep.jwp.presentation.controller.ResourceRequestHandler;
import nextstep.jwp.presentation.exceptionresolver.AuthenticationExceptionResolver;
import nextstep.jwp.presentation.exceptionresolver.BadRequestExceptionResolver;
import nextstep.jwp.presentation.exceptionresolver.NotFoundExceptionResolver;
import org.apache.catalina.servlet.RequestMapping;
import org.apache.catalina.servlet.TomcatConfiguration;
import org.apache.coyote.http11.util.HttpMethod;

public class ServletConfigurationImpl implements ServletConfiguration, TomcatConfiguration {

    @Override
    public void addExceptionResolver() {
        final MappedExceptionResolvers mappedExceptionResolvers = MappedExceptionResolvers.getInstance();
        mappedExceptionResolvers.addResolver(AuthenticationException.class, new AuthenticationExceptionResolver());
        mappedExceptionResolvers.addResolver(NotFoundException.class, new NotFoundExceptionResolver());
        mappedExceptionResolvers.addResolver(BadRequestException.class, new BadRequestExceptionResolver());
    }

    @Override
    public void addRequestHandler() {
        final MappedRequestHandlers mappedRequestHandlers = MappedRequestHandlers.getInstance();
        mappedRequestHandlers.addRequestHandler(RequestHandlerInfo.of("/login", HttpMethod.GET),
                new LoginPageRequestHandler());
        mappedRequestHandlers.addRequestHandler(RequestHandlerInfo.of("/login", HttpMethod.POST),
                new LoginRequestHandler());
        mappedRequestHandlers.addRequestHandler(RequestHandlerInfo.of("/register", HttpMethod.GET),
                new RegisterPageRequestHandler());
        mappedRequestHandlers.addRequestHandler(RequestHandlerInfo.of("/register", HttpMethod.POST),
                new RegisterRequestHandler());
        mappedRequestHandlers.addRequestHandler(RequestHandlerInfo.of("/", HttpMethod.GET),
                new ResourceRequestHandler());
    }

    @Override
    public void addServlet() {
        final RequestMapping requestMapping = RequestMapping.getInstance();
        requestMapping.addServlet("/", new ChicChocServlet());
    }
}
