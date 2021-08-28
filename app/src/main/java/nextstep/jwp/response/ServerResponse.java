package nextstep.jwp.response;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ServerResponse {

    private static final String NEWLINE = "\r\n";
    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private static final String CONTENT_LENGTH = "Content-Length: ";

    private ServerResponse() {
    }

    public static void response(File file, HttpStatusCode statusCode, OutputStream outputStream) throws IOException {
        final String responseBody = readFile(file);
        final String response = String.join(NEWLINE,
                statusHeader(statusCode),
                contentTypeHeader(file),
                contentLengthHeader(responseBody),
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private static String readFile(File file) throws IOException {
        final String path = URLDecoder.decode(file.getAbsolutePath(), StandardCharsets.UTF_8);
        final List<String> fileLines = Files.readAllLines(Path.of(path));

        final StringBuilder responseBodyBuilder = new StringBuilder();
        for (String fileLine : fileLines) {
            responseBodyBuilder.append(fileLine).append(NEWLINE);
        }
        return responseBodyBuilder.toString();
    }

    private static String statusHeader(HttpStatusCode statusCode) {
        return HTTP_VERSION + statusCode.getStatusCode() + " " + statusCode.getStatusText();
    }

    private static String contentTypeHeader(File file) {
        final String fileType = FilenameUtils.getExtension(file.getName());
        return ContentTypeResponse.of(fileType).getContentTypeHeader();
    }

    private static String contentLengthHeader(String responseBody) {
        return CONTENT_LENGTH + responseBody.getBytes().length + " ";
    }
}
