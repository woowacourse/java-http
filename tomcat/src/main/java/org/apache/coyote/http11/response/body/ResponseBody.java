package org.apache.coyote.http11.response.body;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.response.header.MimeType;

public class ResponseBody {

    private final MimeType contentMimeType;
    private final byte[] content;

    // TODO : 빈내용 / 정적 파일 내용 내려주기 / 원하는 문자열(ex. hello world!) 내려주기 & 별도 클래스로 분리하기
    public static ResponseBody createEmptyResponseBody() {
        return new ResponseBody(MimeType.HTML, new byte[0]);
    }

    public static ResponseBody createPlainTextResponseBody(final String content) {
        return new ResponseBody(MimeType.HTML, content.getBytes(StandardCharsets.UTF_8));
    }

    public static ResponseBody createStaticResourceResponseBody(final byte[] content, final String extension) {
        return new ResponseBody(MimeType.of(extension), content);
    }

    public MimeType getContentMimeType() {
        return contentMimeType;
    }

    public int getLength() {
        return content.length;
    }

    public String toResponseText() {
        return new String(content, StandardCharsets.UTF_8);
    }

    private ResponseBody(final MimeType contentMimeType, final byte[] content) {
        this.contentMimeType = contentMimeType;
        this.content = content;
    }
}
