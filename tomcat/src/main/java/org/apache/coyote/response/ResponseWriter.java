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
import org.apache.coyote.request.Request;

public class ResponseWriter {

    private final OutputStream outputStream;

    public ResponseWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void writeResponse(Request request) throws URISyntaxException, IOException {
        BufferedReader bufferedReader = getBufferedReader(request.getPath());
        String response = getResponse(request, bufferedReader);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private BufferedReader getBufferedReader(URL resource) throws URISyntaxException, FileNotFoundException {
        Path resourcePath = Path.of(resource.toURI());
        FileInputStream fileInputStream = new FileInputStream(resourcePath.toFile());
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        return new BufferedReader(inputStreamReader);
    }

    private String getResponse(Request request, BufferedReader bufferedReader) throws IOException {
        String responseBody = getResponseBody(bufferedReader);
        String responseHeader = getResponseHeader(request, responseBody);

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

    private String getResponseHeader(Request request, String responseBody) {
        String status = "200 OK ";
        if (!request.isExists()) {
            status = "404 NOT_FOUND ";
        }

        return String.join("\r\n",
                "HTTP/1.1 " + status,
                "Content-Type: " + request.getResourceTypes() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "");
    }
}
