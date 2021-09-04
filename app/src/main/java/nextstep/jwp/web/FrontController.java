package nextstep.jwp.web;

import nextstep.jwp.exception.InternalServerError;
import nextstep.jwp.exception.InvalidHttpSessionException;
import nextstep.jwp.exception.PageNotFoundError;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;
import nextstep.jwp.response.HttpStatusCode;
import nextstep.jwp.web.model.Cookie;
import nextstep.jwp.web.model.HttpCookie;
import nextstep.jwp.web.model.HttpSession;
import nextstep.jwp.web.model.HttpSessions;

import java.util.Optional;

public class FrontController {
    private static final String INTERNAL_SERVER_ERROR_PAGE = "/500.html";
    private static final String NOT_FOUND_ERROR_PAGE = "/404.html";

    private final RequestMapping requestMapping;

    public FrontController(RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }

    public void getResponse(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            Optional<ControllerMethod> optionalControllerMethod = requestMapping.getControllerMethod(httpRequest);
            if (optionalControllerMethod.isPresent()) {
                ControllerMethod controllerMethod = optionalControllerMethod.orElseThrow(PageNotFoundError::new);
                HttpSession httpSession = this.getHttpSession(httpRequest, httpResponse);
                String viewName = (String) controllerMethod.invoke(httpRequest, httpSession);
                httpResponse.setView(viewName, HttpStatusCode.OK);
                return;
            }
            httpResponse.setView(httpRequest.getResourceName(), HttpStatusCode.OK);
        } catch (InternalServerError e) {
            httpResponse.setView(INTERNAL_SERVER_ERROR_PAGE, HttpStatusCode.INTERNAL_SERVER_ERROR);
        } catch (PageNotFoundError e) {
            httpResponse.setView(NOT_FOUND_ERROR_PAGE, HttpStatusCode.NOTFOUND);
        }
    }

    private HttpSession getHttpSession(HttpRequest httpRequest, HttpResponse httpResponse) {
        HttpCookie httpCookie = httpRequest.getCookies();
        if (hasValidJSessionId(httpCookie)) {
            return HttpSessions.getSession(httpCookie.getJSessionId())
                    .orElseThrow(() -> new InvalidHttpSessionException("세션이 존재하지 않습니다."));
        }

        HttpSession httpSession = HttpSession.create();
        httpResponse.addCookie(new Cookie("JSESSIONID", httpSession.getId()));
        return HttpSessions.save(httpSession);
    }

    private boolean hasValidJSessionId(HttpCookie httpCookie) {
        if (httpCookie.hasJSessionId()) {
            String jSessionId = httpCookie.getJSessionId();
            return HttpSessions.isExistsId(jSessionId);
        }
        return false;
    }

}
