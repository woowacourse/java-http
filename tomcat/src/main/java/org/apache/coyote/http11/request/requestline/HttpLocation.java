package org.apache.coyote.http11.request.requestline;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class HttpLocation {
    public static final String DEFAULT_EXTENSION = "html";
    private static final Pattern validPattern = Pattern.compile("^[A-Za-z0-9-_~./]+$");
    private static final char LOCATION_INNER_DELIMITER = '.';
    public static final String LOCATION_OUTER_DELIMITER = "\\.";

    private final String fileName;
    private final String extension;


    public HttpLocation(String data) {
        Objects.requireNonNull(data, "data can not be null");
        validateDelimiterCount(data);
        List<String> fileAndExtension = Arrays.stream(data.split(LOCATION_OUTER_DELIMITER))
                .collect(Collectors.toList());

        validate(fileAndExtension);

        if (fileAndExtension.size() == 1) {
            fileAndExtension.add(DEFAULT_EXTENSION);
        }
        fileName = fileAndExtension.getFirst();
        extension = fileAndExtension.get(1);
    }

    public static HttpLocation from(String data) {
        return new HttpLocation(data);
    }

    private void validateDelimiterCount(String data) {
        if (data.codePoints().filter(r -> r == LOCATION_INNER_DELIMITER).count() > 1) {
            throw new IllegalArgumentException("location's dot must less than one");
        }
    }

    private void validate(List<String> data) {
        validateEmptiness(data);
        validateFirstCharacter(data);
        validateLength(data);
        validateCharacterWithoutForwardSlash(data);
    }

    private void validateCharacterWithoutForwardSlash(List<String> data) {
        if (data.stream().anyMatch(character -> !validPattern.matcher(character).matches())) {
            throw new IllegalArgumentException("location character parse error");
        }
    }

    private void validateLength(List<String> data) {
        if (data.size() > 2) {
            throw new IllegalArgumentException("location length parse error");
        }
    }

    private void validateFirstCharacter(List<String> data) {
        if (!data.getFirst().startsWith("/")) {
            throw new IllegalArgumentException("location first character parse error");
        }
    }

    private void validateEmptiness(List<String> data) {
        if (data.isEmpty()) {
            throw new IllegalArgumentException("location empty parse error");
        }
    }

    public String getFileName() {
        return fileName;
    }

    public String getExtension() {
        return extension;
    }

    @Override
    public String toString() {
        return "HttpLocation{" +
               "fileName='" + fileName + '\'' +
               ", extension='" + extension + '\'' +
               '}';
    }
}
