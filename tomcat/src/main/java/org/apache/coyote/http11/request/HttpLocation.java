package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpLocation {
    private final String fileName;
    private final String extension;

    private static final Pattern pattern = Pattern.compile("^[A-Za-z0-9-_~.]+$");

    public HttpLocation(String data) {
        List<String> split = Arrays.stream(data.split("\\."))
                .collect(Collectors.toList());
        validate(split);

        fileName = split.removeFirst();
        extension = split.isEmpty() ? "html" : split.getFirst();
    }

    public static HttpLocation from(String data) {
        return new HttpLocation(data);
    }

    private void validate(List<String> split) {
        if (split.isEmpty()) {
            throw new IllegalArgumentException("location empty parse error");
        }
        if (!split.getFirst().startsWith("/")) {
            throw new IllegalArgumentException("location first character parse error");
        }
        split.set(0, split.getFirst().substring(1));
        if(split.stream().noneMatch(r -> pattern.matcher(r).matches())){
            throw new IllegalArgumentException("location character parse error");
        }
        if (split.size() > 2) {
            throw new IllegalArgumentException("location length parse error");
        }
    }


    public String getFileName() {
        return fileName;
    }

    public String getExtension() {
        return extension;
    }
}
