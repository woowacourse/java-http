package org.apache.coyote;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResponseBody {

    private final String value;

    private ResponseBody(String value) {
        this.value = value;
    }

    public static ResponseBody from(String filePath) throws URISyntaxException, IOException {
        return new ResponseBody(getResponseBody(filePath));
    }

    private static String getResponseBody(String uriPath) throws URISyntaxException, IOException {
        if (uriPath.equals("/")) {
            return "Hello world!";
        }
        String fileName = "static" + uriPath;
        final URL resource = ResponseBody.class.getClassLoader().getResource(fileName);
        final File file = Paths.get(resource.toURI()).toFile();
        return new String(Files.readAllBytes(file.toPath()));
    }

    public String getValue() {
        return value;
    }
}
