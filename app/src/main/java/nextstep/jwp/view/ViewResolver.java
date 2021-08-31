package nextstep.jwp.view;

import nextstep.jwp.util.FileUtils;

public class ViewResolver {

    private static final String SUFFIX = ".html";

    public View resolveViewName(String viewName) {
        if (!viewName.endsWith(SUFFIX)) {
            viewName = viewName + SUFFIX;
        }

        String path = FileUtils.getAbsolutePath(viewName);
        return new View(path);
    }
}
