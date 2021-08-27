package nextstep.jwp.http.mapper;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.HelloController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.NotFoundController;
import nextstep.jwp.controller.RedirectController;
import nextstep.jwp.controller.staticpath.CssController;
import nextstep.jwp.controller.staticpath.HtmlController;
import nextstep.jwp.controller.staticpath.JavaScriptController;
import nextstep.jwp.http.HttpPath;
import nextstep.jwp.http.message.request.HttpRequestMessage;

import java.util.HashMap;
import java.util.Map;

public class ControllerMapper {

    private static final Map<String, Controller> mappingInfos;
    private static final Controller htmlController = new HtmlController();
    private static final Controller cssController = new CssController();
    private static final Controller javaScriptController = new JavaScriptController();
    private static final Controller redirectController = new RedirectController();
    private static final Controller notFoundController = new NotFoundController();

    static {
        mappingInfos = new HashMap<>();
        mappingInfos.put("/", new HelloController());
        mappingInfos.put("/login", new LoginController());
    }

    public ControllerMapper() {
    }

    public Controller matchController(HttpRequestMessage httpRequestMessage) {
        HttpPath httpPath = httpRequestMessage.requestPath();
        if (httpPath.isRedirectPath()) {
            return redirectController;
        }
        if (httpPath.isHtmlPath()) {
            return htmlController;
        }
        if (httpPath.isCssPath()) {
            return cssController;
        }
        if (httpPath.isJavaScriptPath()){
            return javaScriptController;
        }
        return mappingInfos.getOrDefault(httpPath.removeQueryString(), notFoundController);
    }


}
