package nextstep.jwp.http.mapper;

import nextstep.jwp.http.controller.Controller;
import nextstep.jwp.controller.HelloController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.http.controller.RedirectController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.http.controller.stationary.CssController;
import nextstep.jwp.http.controller.stationary.HtmlController;
import nextstep.jwp.http.controller.stationary.JavaScriptController;
import nextstep.jwp.http.exception.HttpUriNotFoundException;
import nextstep.jwp.http.common.HttpPath;
import nextstep.jwp.http.message.request.HttpRequestMessage;

import java.util.HashMap;
import java.util.Map;

public class ControllerMapper {

    private static final Map<String, Controller> mappingInfos;
    private static final Controller htmlController = new HtmlController();
    private static final Controller cssController = new CssController();
    private static final Controller javaScriptController = new JavaScriptController();
    private static final Controller redirectController = new RedirectController();

    static {
        mappingInfos = new HashMap<>();
        mappingInfos.put("/", new HelloController());
        mappingInfos.put("/login", new LoginController());
        mappingInfos.put("/register", new RegisterController());
    }

    public ControllerMapper() {
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
        if (!mappingInfos.containsKey(uri)) {
            throw new HttpUriNotFoundException(String.format("해당 uri의 매핑을 찾을 수 없습니다.(%s)", uri));
        }
        return mappingInfos.get(uri);
    }
}
