package mvc.controller;

import mvc.controller.exception.InvalidParameterException;
import mvc.controller.mapping.RequestMapping;
import mvc.controller.mapping.exception.UnsupportedHttpRequestException;
import nextstep.jwp.application.exception.AlreadyExistsAccountException;
import nextstep.jwp.application.exception.LoginFailureException;
import servlet.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlet.Controller;
import servlet.response.HttpResponse;

public class FrontController extends AbstractPathController {

    private static final Logger log = LoggerFactory.getLogger(FrontController.class);

    private final RequestMapping requestMapping;

    public FrontController(final RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        try {
            final Controller controller = requestMapping.getController(request);

            controller.service(request, response);
        } catch (final LoginFailureException | AlreadyExistsAccountException | InvalidParameterException e) {
            log.warn("warn : ", e);
            response.sendRedirect("/400.html");
        } catch (final UnsupportedHttpRequestException e) {
            log.warn("warn : ", e);
            response.sendRedirect("/404.html");
        } catch (final Exception e) {
            log.error("error : ", e);
            response.sendRedirect("/500.html");
        }
    }
}
