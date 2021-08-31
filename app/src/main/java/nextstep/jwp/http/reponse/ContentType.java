package nextstep.jwp.http.reponse;

import java.util.Arrays;

public enum ContentType {

    HTML(".html","text/html;charset=utf-8"),
    JS(".js", "text/javascript"),
    CSS(".css","text/css,*/*;q=0.1");

    private String fileExtension;
    private String contentType;

    ContentType(String fileExtension, String contentType) {
        this.fileExtension = fileExtension;
        this.contentType = contentType;
    }

    static String matchByFileExtension(String fileName) {
        return Arrays.stream(ContentType.values())
            .filter(it -> fileName.endsWith(it.fileExtension))
            .findAny()
            .orElseThrow(IllegalArgumentException::new)
            .contentType;
    }

}

