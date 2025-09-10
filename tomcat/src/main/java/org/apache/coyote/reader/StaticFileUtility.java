package org.apache.coyote.reader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.coyote.exception.FileReadException;
import org.apache.coyote.exception.InvalidRequestException;
import org.apache.coyote.http.value.ContentType;

public class StaticFileUtility {

    private static String BASE_URI = "static/";

    public static boolean isExistFile(String fileUri) {
        ClassLoader classLoader = StaticFileUtility.class.getClassLoader();
        URL resourceUrl = classLoader.getResource(BASE_URI + fileUri);
        return resourceUrl != null;
    }

    public static String readFile(String fileUri) {
        try {
            validateInvalidUri(fileUri);
            ClassLoader classLoader = StaticFileUtility.class.getClassLoader();
            URL resourceUrl = classLoader.getResource(BASE_URI + fileUri);
            Path path = Paths.get(resourceUrl.toURI());
            return Files.readString(path);
        } catch (URISyntaxException | IOException e) {
            throw new FileReadException("파일을 읽는데 오류가 발생했습니다.");
        }
    }

    public static ContentType getFileExtension(String fileUri) {
        String decodedUri = URLDecoder.decode(fileUri, StandardCharsets.UTF_8);
        List<String> uriPart = List.of(decodedUri.split("/"));
        String fileName = uriPart.getLast();
        List<String> split = List.of(fileName.split("\\."));
        return ContentType.parse(split.getLast());
    }

    private static void validateInvalidUri(String fileUri) {
        String decodedUri = URLDecoder.decode(fileUri, StandardCharsets.UTF_8);
        List<String> uriPart = List.of(decodedUri.split("/"));
        if (uriPart.contains(".") || uriPart.contains("..")) {
            throw new InvalidRequestException("부적절한 리소스 주소입니다.");
        }
    }
}
