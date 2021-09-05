package nextstep.jwp.constants;

public enum Header {
    CONTENT_LENGTH("Content-Length", "0"),
    LOCATION("Location"),
    CONTENT_TYPE("Content-Type", "text/html;charset=utf-8"),
    SET_COOKIE("Set-Cookie"),
    COOKIE("Cookie");

    private final String type;
    private String value;

    Header(String type) {
        this.type = type;
    }

    Header(String type, String value){
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return this.type;
    }

    public String getValue(){
        return this.value;
    }
}
