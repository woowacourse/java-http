package nextstep.jwp.view;

import nextstep.jwp.util.FileUtils;

public class ViewResolver {

    public static final String PREFIX = "/";
    private static final String SUFFIX = ".html";

    public String resolve(String viewName) {
        if (!viewName.startsWith(PREFIX)) {
            viewName = PREFIX + viewName;
        }

        if (!viewName.endsWith(SUFFIX)) {
            viewName = viewName + SUFFIX;
        }

        return FileUtils.getAbsolutePath(viewName);
    }
}
