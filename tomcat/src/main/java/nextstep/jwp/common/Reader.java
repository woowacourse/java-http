package nextstep.jwp.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Reader {

    private Reader() {
    }

    public static Map<String, String> readUserInfo(String body) {
        return Arrays.stream(body.split("&"))
                .map(value -> value.split("="))
                .collect(Collectors.toMap(value -> value[0], value -> value[1]));
    }

    public static byte[] readBytes(Path path) {
        try {
            byte[] bytes = Files.readAllBytes(path);
            return bytes;
        } catch (IOException e) {
        }
        return null;
    }
}
