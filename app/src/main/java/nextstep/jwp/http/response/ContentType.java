package nextstep.jwp.http.response;

import java.util.Arrays;
import nextstep.jwp.resource.FileType;

public enum ContentType {
    HTML("text/html;charset=utf-8", FileType.HTML),
    PLAIN_TEXT("text/plain;charset=utf-8", FileType.PLAIN_TEXT),
    JS("text/js", FileType.JS),
    CSS("text/css", FileType.CSS),
    ALL("*/*", null);


    private String text;
    private FileType fileType;

    ContentType(String text, FileType fileType) {
        this.text = text;
        this.fileType = fileType;
    }

    public static ContentType findByFileType(FileType fileType) {
        return Arrays.stream(values())
            .filter(contentType -> contentType.fileType.equals(fileType))
            .findAny()
            .orElse(ALL);
    }

    public String getText() {
        return text;
    }

    public FileType getFileType() {
        return fileType;
    }
}
