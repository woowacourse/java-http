package nextstep.jwp.http.response;

public class ContentType {

    public static final ContentType CSS_UTF8 = new ContentType("text/css;charset=utf-8");
    public static final ContentType HTML_UTF8 = new ContentType("text/html;charset=utf-8");
    public static final ContentType PLAIN_UTF8 = new ContentType("text/plain;charset=utf-8");
    public static final ContentType TEXT_DEFAULT_TYPE = PLAIN_UTF8;

    private final String contentType;

    private ContentType(String contentType) {
        this.contentType = contentType;
    }

    public static ContentType parseFromExtension(String extension) {
        if(extension.equals("css")){
            return CSS_UTF8;
        }
        if(extension.equals("html")){
            return HTML_UTF8;
        }
        return PLAIN_UTF8;
    }

    public static ContentType empty() {
        return TEXT_DEFAULT_TYPE;
    }

    public String value(){
        return contentType;
    }
}
