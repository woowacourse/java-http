package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpVersion;
import org.apache.coyote.http11.common.ResponseStatus;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

public class FileHandler implements Handler {
    @Override
    public ResponseEntity handle(HttpRequest request) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        System.out.println("handler: " + request.getEndPoint());
        URL resource = classLoader.getResource("static" + request.getEndPoint());

        File file = new File(resource.getFile());
        String fileData = new String(Files.readAllBytes(file.toPath()));

        List<String> headers = List.of(
                String.join(" ", "Content-Type:", ContentType.findMatchingType(request.getEndPoint()).getContentType()),
                String.join(" ", "Content-Length:", String.valueOf(fileData.getBytes().length))
        );

        return new ResponseEntity(HttpVersion.HTTP_1_1, ResponseStatus.OK, headers, fileData);
    }
}
