package nextstep.jwp.http.mapper;

import nextstep.jwp.controller.MappingAdvice;
import nextstep.jwp.http.common.HttpPath;
import nextstep.jwp.http.controller.Controller;
import nextstep.jwp.http.controller.stationary.CssController;
import nextstep.jwp.http.controller.stationary.HtmlController;
import nextstep.jwp.http.controller.stationary.JavaScriptController;
import nextstep.jwp.http.controller.stationary.RedirectController;
import nextstep.jwp.http.exception.HttpUriNotFoundException;
import nextstep.jwp.http.message.request.HttpRequestMessage;

public class ControllerMapper {

    private static final ControllerMapper instance = new ControllerMapper();
    private static final MappingAdvice mappingAdvice = MappingAdvice.getInstance();
    private static final Controller htmlController = new HtmlController();
    private static final Controller cssController = new CssController();
    private static final Controller javaScriptController = new JavaScriptController();
    private static final Controller redirectController = new RedirectController();

    private ControllerMapper() {
    }

    public static ControllerMapper getInstance() {
        return instance;
    }

    public Controller matchController(HttpRequestMessage httpRequestMessage) {
        HttpPath httpPath = httpRequestMessage.requestPath();
        final String uri = httpPath.removeQueryString();
        if (httpPath.isRedirectPath()) {
            return redirectController;
        }
        if (httpPath.isHtmlPath()) {
            return htmlController;
        }
        if (httpPath.isCssPath()) {
            return cssController;
        }
        if (httpPath.isJavaScriptPath()) {
            return javaScriptController;
        }
        return mappingAdvice.matchController(uri)
                .orElseThrow(
                        () -> new HttpUriNotFoundException(String.format("해당 uri의 매핑을 찾을 수 없습니다.(%s)", uri))
                );
    }
}
