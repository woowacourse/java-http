package nextstep.jwp.model;

import java.util.List;

public class FileName {

    private static final String BLANK_DELIMITER = " ";
    private static final String EXTENSION_DELIMITER = "\\.";
    private static final String QUERY_STRING_DELIMITER = "\\?";
    private static final String QUERY_STRING_VALUE = "?";
    private static final String EACH_QUERY_STRING_DELIMITER = "&";
    private static final String ROOT_DIR = "/";
    private static final String DEFAULT_FILE_NAME = "Hello world!";
    private static final String DEFAULT_EXTENSION = "html";
    private static final int SPLIT_SIZE = 2;
    private static final String CONCAT_DELIMITER = ".";

    private final String baseName;
    private final String extension;
    private final FormData queries;

    private FileName(String baseName, String extension, FormData queries) {
        this.baseName = baseName;
        this.extension = extension;
        this.queries = queries;
    }

    public static FileName from(List<String> rawRequest) {
        if (rawRequest.isEmpty()) {
            throw new IllegalArgumentException("Request는 최소 한 줄 이상이어야 합니다.");
        }
        String[] firstLine = rawRequest.get(0).split(BLANK_DELIMITER);
        if (firstLine.length < 2) {
            throw new IllegalArgumentException("HTTP URL을 파싱할 수 없습니다.");
        }
        String url = firstLine[1];
        return getFileName(url);
    }

    private static FileName getFileName(String url) {
        String queries = "";
        if (url.contains(QUERY_STRING_VALUE)) {
            String[] splitByQuery = url.split(QUERY_STRING_DELIMITER);
            url = splitByQuery[0];
            queries = splitByQuery[1];
        }
        FormData formData = FormData.from(queries.split(EACH_QUERY_STRING_DELIMITER));
        if (url.equals(ROOT_DIR)) {
            return new FileName(DEFAULT_FILE_NAME, DEFAULT_EXTENSION, formData);
        }
        String[] parsedFileName = url.split(EXTENSION_DELIMITER);
        if (parsedFileName.length < SPLIT_SIZE) {
            return new FileName(parsedFileName[0], DEFAULT_EXTENSION, formData);
        }
        return new FileName(parsedFileName[0], parsedFileName[1], formData);
    }

    public String concat() {
        return baseName + CONCAT_DELIMITER + extension;
    }

    public boolean isSame(String baseName) {
        return this.baseName.equals(baseName);
    }

    public String getBaseName() {
        return baseName;
    }

    public String getExtension() {
        return extension;
    }
}
