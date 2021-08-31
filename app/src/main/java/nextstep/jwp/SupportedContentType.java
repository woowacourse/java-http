package nextstep.jwp;

public enum SupportedContentType implements ResponseHeaderable {
    HTML("Content-Type: text/html;charset=utf-8"),
    CSS("Content-Type: text/css;charset=utf-8"),
    JS("Content-Type: application/js;charset=utf-8");

    private final String responseHeaderName;

    SupportedContentType(String responseHeaderName) {
        this.responseHeaderName = responseHeaderName;
    }

    @Override
    public Boolean isEmpty() {
        return this.responseHeaderName == null;
    }

    @Override
    public String getHttpHeaderToString() {
        return responseHeaderName;
    }
}
