package org.apache.coyote.http11.response;

public class ContentTypeResolver {

    public static MimeType getContentTypeByExtension(String path) {
        int dotIndex = path.lastIndexOf(".");
        if (dotIndex == -1) {
            // 처리할 수 없는 MIME 타입인 경우, 기본(fallback)을 application/octet-stream으로 처리한다.
            // 참고: https://developer.mozilla.org/ko/docs/Web/HTTP/Guides/MIME_types/Common_types
            return MimeType.BINARY;
        }

        // 확장자 추출
        String extension = path.substring(dotIndex + 1);
        return MimeType.fromExtension(extension);
    }
}
