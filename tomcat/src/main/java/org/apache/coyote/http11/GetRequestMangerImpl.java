package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GetRequestMangerImpl implements RequestManager {

    private static final Integer STATUS_CODE = 200;
    private static final String OK = "OK";

    private final RequestParser requestParser;

    public GetRequestMangerImpl(RequestParser requestParser) {
        this.requestParser = requestParser;
    }

    @Override
    public String generateResponse() throws IOException {
        FileName fileName = requestParser.generateFileName();

        String responseBody = fileName.getPrefix();
        System.out.println(fileName.getPrefix() + " " + fileName.getExtension());
        if (!responseBody.equals("Hello world!")) {
            URL resource = getClass().getClassLoader().getResource("static/" + responseBody + "." + fileName.getExtension());
            final Path path = new File(resource.getFile()).toPath();
            final List<String> actual = Files.readAllLines(path);
            responseBody = String.join("\n", actual) + "\n";
        }

        return String.join("\r\n",
                "HTTP/1.1 " + STATUS_CODE + " " + OK + " ",
                "Content-Type: text/" + fileName.getExtension() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
