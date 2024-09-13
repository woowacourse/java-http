package org.apache.coyote.http11.file;

import org.apache.coyote.exception.UncheckedHttpException;

public record FileDetails(String fileName, FileExtension extension) {

    private static final String ROOT_PATH = "/";
    private static final String DEFAULT_FILE_PATH = "/index";
    private static final String EXTENSION_DELIMITER = ".";
    private static final int NOT_MATCHED_INDEX = -1;

    public static FileDetails from(String path) {
        if (ROOT_PATH.equals(path)) {
            return new FileDetails(DEFAULT_FILE_PATH, FileExtension.HTML);
        }
        int extensionIndex = path.lastIndexOf(EXTENSION_DELIMITER);
        validateEndsWithDelimiter(path, extensionIndex);
        if (extensionIndex == NOT_MATCHED_INDEX) {
            return new FileDetails(path, FileExtension.HTML);
        }
        String filePath = path.substring(0, extensionIndex);
        String extension = path.substring(extensionIndex);
        FileExtension fileExtension = FileExtension.from(extension);
        return new FileDetails(filePath, fileExtension);
    }

    private static void validateEndsWithDelimiter(String path, int extensionIndex) {
        int lastIndexOfPath = path.length() - 1;
        if (extensionIndex == lastIndexOfPath) {
            throw new UncheckedHttpException(new IllegalArgumentException("요청 경로는 확장자 구분자으로 끝날 수 없습니다."));
        }
    }

    public String getFilePath() {
        return fileName + extension.getExtension();
    }
}
