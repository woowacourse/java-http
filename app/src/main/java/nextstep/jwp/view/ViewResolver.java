package nextstep.jwp.view;

import java.io.File;
import java.net.URL;
import nextstep.jwp.ServerConfig;

public class ViewResolver {

    private static final String ROOT_RESPONSE = ServerConfig.ROOT_RESPONSE;
    private static final String RESOURCE_BASE_PATH = ServerConfig.RESOURCE_BASE_PATH;

    public View resolve(String viewName) {
        try {
            if (viewName.isEmpty()) {
                return View.empty();
            }

            if (viewName.equals("/")) {
                return View.asString(ROOT_RESPONSE);
            }

            final URL resourceUrl = getClass().getResource(RESOURCE_BASE_PATH + viewName);
            return View.asFile(new File(resourceUrl.getFile()));
        } catch (Exception e) {
            throw new IllegalArgumentException("view not found");
        }
    }
}
