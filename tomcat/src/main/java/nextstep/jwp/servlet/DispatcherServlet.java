package nextstep.jwp.servlet;

import nextstep.jwp.controller.FileController;
import nextstep.jwp.controller.HelloWorldController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.exception.HandlerMappingException;
import nextstep.jwp.handle.HandlerMapping;
import nextstep.jwp.handle.HandlerMethod;
import nextstep.jwp.handle.mapping.GetFileMappingInfo;
import nextstep.jwp.handle.mapping.GetHelloWorldMappingInfo;
import nextstep.jwp.handle.mapping.GetLoginMappingInfo;
import nextstep.jwp.handle.mapping.GetRegisterMappingInfo;
import nextstep.jwp.handle.mapping.PostLoginMappingInfo;
import nextstep.jwp.handle.mapping.PostRegisterMappingInfo;
import org.apache.catalina.core.Servlet;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class DispatcherServlet implements Servlet {

    private HandlerMapping handlerMapping;

    public DispatcherServlet() {
        try {
            this.handlerMapping = new HandlerMapping();
            handlerMapping.addMappingInfo(
                    new GetFileMappingInfo(),
                    new HandlerMethod(
                            FileController.getInstance(),
                            FileController.class.getDeclaredMethod("doGet", HttpRequest.class, HttpResponse.class)
                    )
            );
            handlerMapping.addMappingInfo(
                    new GetHelloWorldMappingInfo(),
                    new HandlerMethod(
                            HelloWorldController.getInstance(),
                            HelloWorldController.class.getDeclaredMethod("doGet", HttpRequest.class, HttpResponse.class)
                    )
            );
            handlerMapping.addMappingInfo(
                    new GetLoginMappingInfo(),
                    new HandlerMethod(
                            LoginController.getInstance(),
                            LoginController.class.getDeclaredMethod("doGet", HttpRequest.class, HttpResponse.class)
                    )
            );
            handlerMapping.addMappingInfo(
                    new PostLoginMappingInfo(),
                    new HandlerMethod(
                            LoginController.getInstance(),
                            LoginController.class.getDeclaredMethod("doPost", HttpRequest.class, HttpResponse.class)
                    )
            );
            handlerMapping.addMappingInfo(
                    new GetRegisterMappingInfo(),
                    new HandlerMethod(
                            RegisterController.getInstance(),
                            RegisterController.class.getDeclaredMethod("doGet", HttpRequest.class, HttpResponse.class)
                    ));
            handlerMapping.addMappingInfo(
                    new PostRegisterMappingInfo(),
                    new HandlerMethod(
                            RegisterController.getInstance(),
                            RegisterController.class.getDeclaredMethod("doPost", HttpRequest.class, HttpResponse.class)
                    )
            );
        } catch (NoSuchMethodException e) {
            throw new HandlerMappingException();
        }
    }

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        final HandlerMethod handlerMethod = handlerMapping.getHandlerMethod(request);
        handlerMethod.invokeMethod(request, response);
    }
}
