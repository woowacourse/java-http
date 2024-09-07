package org.apache.coyote.view;

public class View {

    private final String content;

    public View(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
