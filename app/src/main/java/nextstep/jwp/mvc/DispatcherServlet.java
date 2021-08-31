package nextstep.jwp.mvc;

import nextstep.jwp.core.ApplicationContext;
import nextstep.jwp.core.exception.NotFoundBeanException;
import nextstep.jwp.mvc.handler.Handler;
import nextstep.jwp.mvc.mapping.HandlerMapping;
import nextstep.jwp.mvc.mapping.MethodHandlerMapping;
import nextstep.jwp.mvc.view.ModelAndView;
import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.response.HttpResponse;

public class DispatcherServlet {

    private ApplicationContext applicationContext;
    private HandlerMapping handlerMapping;
    private StaticResourceHandler staticResourceHandler;

    public DispatcherServlet(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        initStrategy();
    }

    private void initStrategy() {
        initHandlerMapping();
        initStaticResourceHandler();
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
        Exception exception = null;
        try {
            final Handler handler = handlerMapping.findHandler(httpRequest);
            if (handler == null) {
                staticResourceHandle(httpRequest, httpResponse);
                return;
            }
            final ModelAndView modelAndView = handler.doRequest(httpRequest, httpResponse);

        } catch (Exception e) {
            exception = e;
        }

    }

    private void staticResourceHandle(HttpRequest httpRequest, HttpResponse httpResponse) {
        staticResourceHandler.handleResource(httpRequest, httpResponse);
    }
}
