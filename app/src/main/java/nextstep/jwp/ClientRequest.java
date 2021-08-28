package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public enum ClientRequest {
    INDEX_PAGE("index.html"),
    ERROR_PAGE("error");

    private String pageName;

    ClientRequest(String pageName) {
        this.pageName = pageName;
    }

    public static ClientRequest of(String requestFirstLine) {
        return Arrays.stream(values())
                .filter(clientRequest -> requestFirstLine.contains(clientRequest.pageName))
                .findAny()
                .orElse(ERROR_PAGE);
    }

    public String generateResponse() throws IOException {
        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = new File(Objects.requireNonNull(classLoader.getResource("static/index.html")).getFile());
        final String path = URLDecoder.decode(file.getAbsolutePath(), StandardCharsets.UTF_8);
        final List<String> fileLines = Files.readAllLines(Path.of(path));

        final StringBuilder responseFile = new StringBuilder();

        for (String fileLine : fileLines) {
            responseFile.append(fileLine).append("\r\n");
        }

        final String responseBody = responseFile.toString();

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
