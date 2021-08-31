package nextstep.jwp.mvc;

import nextstep.jwp.core.ApplicationContext;
import nextstep.jwp.core.exception.NotFoundBeanException;
import nextstep.jwp.mvc.exceptionresolver.ExceptionResolverContainer;
import nextstep.jwp.mvc.handler.Handler;
import nextstep.jwp.mvc.mapping.HandlerMapping;
import nextstep.jwp.mvc.mapping.MethodHandlerMapping;
import nextstep.jwp.mvc.view.DefaultViewResolver;
import nextstep.jwp.mvc.view.ModelAndView;
import nextstep.jwp.mvc.view.ViewResolver;
import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.response.HttpResponse;

public class DispatcherServlet {

    private ApplicationContext applicationContext;
    private HandlerMapping handlerMapping;
    private StaticResourceHandler staticResourceHandler;
    private ViewResolver viewResolver;
    private ExceptionResolverContainer exceptionResolverContainer;

    public DispatcherServlet(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        initStrategy();
    }

    private void initStrategy() {
        initHandlerMapping();
        initStaticResourceHandler();
        this.viewResolver = new DefaultViewResolver();
        this.exceptionResolverContainer = new ExceptionResolverContainer();
    }

    private void initHandlerMapping() {
        try {
            this.handlerMapping =
                    applicationContext.getBean(HandlerMapping.class);
        } catch (NotFoundBeanException e) {
            this.handlerMapping = new MethodHandlerMapping(applicationContext);
            applicationContext.insertBean(this.handlerMapping);
        }
    }

    private void initStaticResourceHandler() {
        try {
            this.staticResourceHandler =
                    applicationContext.getBean(StaticResourceHandler.class);
        } catch (NotFoundBeanException e) {
            this.staticResourceHandler =
                    new DefaultStaticResourceHandler(applicationContext);
            applicationContext.insertBean(staticResourceHandler);
        }
    }

    public void doDispatch(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            final Handler handler = handlerMapping.findHandler(httpRequest);
            if (handler == null) {
                staticResourceHandle(httpRequest, httpResponse);
                return;
            }
            ModelAndView modelAndView = handler.doRequest(httpRequest, httpResponse);
            viewResolver.render(modelAndView, httpRequest, httpResponse);
        } catch (Exception e) {
            resolveException(e, httpRequest, httpResponse);
        }

    }

    private void staticResourceHandle(HttpRequest httpRequest, HttpResponse httpResponse) {
        staticResourceHandler.handleResource(httpRequest, httpResponse);
    }

    private void resolveException(Exception e, HttpRequest httpRequest, HttpResponse httpResponse) {
        exceptionResolverContainer.resolve(e, httpRequest, httpResponse);
    }
}
