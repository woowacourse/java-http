package nextstep.jwp.network;

public enum ContentType {
    HTML("text/html;charset=utf-8"),
    CSS("text/css");
    
    private final String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
