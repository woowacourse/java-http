package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class RequestHandler {

    public static String handle(HttpRequest request) throws IOException {
        if (request.getTarget().equals("/")) {
            return getResponse("Hello world!", ContentType.HTML);
        } else {
            String fileUrl = "static" + request.getTarget();
            File file = new File(
                RequestHandler.class
                    .getClassLoader()
                    .getResource(fileUrl)
                    .getFile()
            );
            String responseBody = new String(Files.readAllBytes(file.toPath()));
            return getResponse(responseBody, ContentType.from(file.getName()));
        }
    }

    private static String getResponse(String responseBody, ContentType contentType) {
        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: "+ contentType.value  + ";charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }
}
