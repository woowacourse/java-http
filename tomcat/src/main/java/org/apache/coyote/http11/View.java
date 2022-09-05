package org.apache.coyote.http11;

public class View {

    private static final String DEFAULT_PATH = "/";
    private static final String REDIRECT = "redirect:";
    public static final String EXTENSION = ".html";

    private final String value;
    private final boolean isRedirect;

    private View(final String value, final boolean isRedirect) {
        this.value = value;
        this.isRedirect = isRedirect;
    }

    public static View from(final String view) {
        if (view.startsWith(REDIRECT)) {
            int index = view.indexOf(DEFAULT_PATH);
            return new View(DEFAULT_PATH + view.substring(index + 1) + EXTENSION, true);
        }
        return new View(DEFAULT_PATH + view + EXTENSION, false);
    }

    public String getValue() {
        return value;
    }

    public boolean isRedirect() {
        return isRedirect;
    }
}
