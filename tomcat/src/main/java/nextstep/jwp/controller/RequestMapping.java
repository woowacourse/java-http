package nextstep.jwp.controller;

import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.catalina.Controller;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class RequestMapping implements Controller {

    private final List<AbstractController> controllers = new ArrayList<>();
    private AbstractController defaultController;

    public RequestMapping(final List<AbstractController> controllers, final AbstractController defaultController) {
        this.controllers.addAll(controllers);
        this.defaultController = defaultController;
    }

    // jwp에서 requestMapping에 미리 controller들을 등록한 상황이라고 가정
    public static Controller getDefault() {
        return new RequestMapping(List.of(
                new LoginController(),
                new LoginPageController(),
                new RegisterController(),
                new RegisterPageController(),
                new MainPageController(),
                new StaticResourceController()
        ), new ErrorPageController());
    }

    public Controller getController(final HttpRequest request) {
        return controllers.stream()
                .filter(controller -> controller.canHandle(request))
                .findAny()
                .orElse(defaultController);
    }

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        try {
            getController(httpRequest).service(httpRequest, httpResponse);
        } catch (Exception e) {
            throw new UncheckedServletException(e);
        }
    }
}
