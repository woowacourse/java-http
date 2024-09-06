package org.apache.coyote.http11.response.view;

import org.apache.coyote.http11.util.StaticFileUtils;

public class View {

    private final ViewType type;
    private final String content;

    public View(ViewType type, String content) {
        this.type = type;
        this.content = content;
    }

    public static View createByStaticResource(String path) {
        return new View(ViewType.HTML, StaticFileUtils.readStaticFile(path));
    }

    public static View createByContent(String content) {
        return new View(ViewType.HTML, content);
    }

    public String getContent() {
        return content;
    }

    public ViewType getType() {
        return type;
    }
}
