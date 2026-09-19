package org.apache.coyote.http11.pageController;

import java.util.Map;

public class PageControllerMapper {
    private static final PageController DEFAULT_CONTROLLER = new StaticResourceController();
    private static final Map<String, PageController> mapper = Map.of(
            "/login", new LoginController()
    );

    private PageControllerMapper() {
    }

    public static PageController getPageController(String path) {
        return mapper.getOrDefault(path, DEFAULT_CONTROLLER);
    }
}
