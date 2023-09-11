package org.apache.coyote.http11;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.DefaultController;
import org.apache.coyote.http11.controller.IndexController;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.RootController;
import org.apache.coyote.http11.controller.UnAuthorizedController;
import org.apache.coyote.http11.controller.Uri;
import org.apache.coyote.http11.request.HttpRequest;

import java.util.EnumMap;
import java.util.Map;


public class RequestMapping {
    private final Map<Uri, Controller> controllerMap;

    public RequestMapping() {
        this.controllerMap = new EnumMap<>(Uri.class);
        controllerMap.put(Uri.ROOT, new RootController());
        controllerMap.put(Uri.INDEX, new IndexController());
        controllerMap.put(Uri.LOGIN, new LoginController());
        controllerMap.put(Uri.UNAUTHORIZED, new UnAuthorizedController());
        controllerMap.put(Uri.REGISTER, new RegisterController());
        controllerMap.put(Uri.DEFAULT, new DefaultController());
    }

    public Controller getController(final HttpRequest httpRequest) {
        final String path = httpRequest.getRequestLine().getPath();
        final Uri uri = Uri.from(path);
        return controllerMap.get(uri);
    }
}
