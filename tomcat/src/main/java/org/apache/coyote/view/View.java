package org.apache.coyote.view;

import java.util.Objects;

public class View {

    private final String content;

    public View(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        View view = (View) o;
        return Objects.equals(content, view.content);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(content);
    }
}
