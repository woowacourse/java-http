package org.apache.coyote.http11.mvc.view;

public class SimpleStringDataView implements View {

    private static final String CONTENT_TYPE = "text/plain;charset=utf-8";

    private final String data;

    private SimpleStringDataView(final String data) {
        this.data = data;
    }

    public static SimpleStringDataView from(final String data) {
        return new SimpleStringDataView(data);
    }

    @Override
    public String renderView() {
        return data;
    }

    @Override
    public String getContentType() {
        return CONTENT_TYPE;
    }
}
