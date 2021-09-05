package nextstep.jwp.constants;

public enum HeaderType {
    CONTENT_LENGTH("Content-Length", "0"),
    LOCATION("Location"),
    CONTENT_TYPE("Content-Type", "text/html;charset=utf-8"),
    SET_COOKIE("Set-Cookie"),
    COOKIE("Cookie");

    private final String value;
    private String defaultValue;

    HeaderType(String value) {
        this.value = value;
    }

    HeaderType(String value, String defaultValue){
        this.value = value;
        this.defaultValue = defaultValue;
    }

    public String getValue() {
        return this.value;
    }

    public String getDefaultValue(){
        return this.defaultValue;
    }
}
