package org.apache.coyote.http11.request;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import org.apache.coyote.exception.UncheckedHttpException;
import org.apache.coyote.http11.component.FileExtension;
import org.apache.coyote.http11.file.FileDetails;

public record HttpRequestUri(URI uri, Map<String, String> queryParams) {

    private static final URI ROOT_URI = URI.create("/");
    private static final URI DEFAULT_FILE_URI = URI.create("/index");
    private static final String EXTENSION_DELIMITER = ".";
    private static final int NOT_MATCHED_INDEX = -1;

    public HttpRequestUri(URI uri) {
        this(uri, Collections.emptyMap());
    }

    public FileDetails getFileDetails() {
        if (ROOT_URI.equals(uri)) {
            return new FileDetails(DEFAULT_FILE_URI.getPath());
        }
        String path = uri.getPath();
        int extensionIndex = path.lastIndexOf(EXTENSION_DELIMITER);
        validateEndsWithDelimiter(path, extensionIndex);
        if (extensionIndex == NOT_MATCHED_INDEX) {
            return new FileDetails(path);
        }
        String filePath = path.substring(0, extensionIndex);
        String extension = path.substring(extensionIndex);
        FileExtension fileExtension = FileExtension.from(extension);
        return new FileDetails(filePath, fileExtension);
    }

    private void validateEndsWithDelimiter(String path, int extensionIndex) {
        int lastIndexOfPath = path.length() - 1;
        if (extensionIndex == lastIndexOfPath) {
            throw new UncheckedHttpException(new IllegalArgumentException("요청 URI는 확장자 구분자으로 끝날 수 없습니다."));
        }
    }
}
