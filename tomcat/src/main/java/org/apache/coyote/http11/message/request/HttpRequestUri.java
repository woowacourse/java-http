package org.apache.coyote.http11.message.request;

import java.util.Optional;

import org.apache.coyote.http11.FileExtensionType;

public class HttpRequestUri {

    private static final String FILE_NAME_AND_EXTENSION_SEPARATOR = "\\.";
    private static final int FILE_EXTENSION_INDEX = 1;

    private final String value;

    public HttpRequestUri(final String value) {
        validateRequestUriValueIsNullOrBlank(value);
        validateRequestUriStartWithSlash(value);
        this.value = value;
    }

    private void validateRequestUriValueIsNullOrBlank(final String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Request URI 값으로 null 혹은 빈 값이 입력될 수 없습니다. - " + value);
        }
    }

    private void validateRequestUriStartWithSlash(final String value) {
        if (!value.startsWith("/")) {
            throw new IllegalArgumentException("Request URI 값은 /로 시작해야합니다. - " + value);
        }
    }

    public String getValue() {
        return value;
    }

    public boolean matchRequestUri(final String uri) {
        return value.equals(uri);
    }

    public boolean isStaticResourceUri() {
        return parseStaticFileExtensionType().isPresent();
    }

    public Optional<FileExtensionType> parseStaticFileExtensionType() {
        final String[] fileNameAndExtension = value.split(FILE_NAME_AND_EXTENSION_SEPARATOR);
        if (fileNameAndExtension.length != 2) {
            return Optional.empty();
        }

        final String extension = fileNameAndExtension[FILE_EXTENSION_INDEX];
        return Optional.of(FileExtensionType.getByValue(extension));
    }

    public Optional<FileExtensionType> getFileExtensionType() {
        return parseStaticFileExtensionType();
    }
}
