package org.apache.coyote.response;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

public class ResponseWriter {

    private static final String NOT_FOUND_RESOURCE_PATH = "/static/404.html";

    private final OutputStream outputStream;

    public ResponseWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void writeResponse(URL resource) throws URISyntaxException, IOException {
        boolean isExistFile = true;
        if (resource == null) {
            resource = getClass().getResource(NOT_FOUND_RESOURCE_PATH);
            isExistFile = false;
        }
        BufferedReader bufferedReader = getBufferedReader(resource);
        String response = getResponse(isExistFile, bufferedReader);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private BufferedReader getBufferedReader(URL resource) throws URISyntaxException, FileNotFoundException {
        Path resourcePath = Path.of(resource.toURI());
        FileInputStream fileInputStream = new FileInputStream(resourcePath.toFile());
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        return new BufferedReader(inputStreamReader);
    }

    private String getResponse(boolean isExistFile, BufferedReader bufferedReader) throws IOException {
        String responseBody = getResponseBody(bufferedReader);
        String responseHeader = getResponseHeader(isExistFile, responseBody);

        return String.join("\r\n",
                responseHeader,
                responseBody);
    }

    private String getResponseBody(BufferedReader bufferedReader) throws IOException {
        StringBuilder responseBodyBuilder = new StringBuilder();
        String fileLine;
        while ((fileLine = bufferedReader.readLine()) != null) {
            responseBodyBuilder.append(fileLine + "\n");
        }

        return responseBodyBuilder.toString();
    }

    private String getResponseHeader(boolean isExistFile, String responseBody) {
        String status = "200 OK ";
        if (!isExistFile) {
            status = "404 NOT_FOUND ";
        }
        return String.join("\r\n",
                "HTTP/1.1 " + status,
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "");
    }
}
