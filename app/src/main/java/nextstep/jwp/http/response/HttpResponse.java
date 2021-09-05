package nextstep.jwp.http.response;

import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.exception.HttpFormatException;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.google.common.net.HttpHeaders.*;

public class  HttpResponse {
    private final DataOutputStream dataOutputStream;
    private final Map<String, String> headers;
    private final ResponseLine responseLine;
    private String resource;

    public HttpResponse(OutputStream outputStream, String httpVersion) {
        this.dataOutputStream = new DataOutputStream(outputStream);
        this.responseLine = new ResponseLine(httpVersion);
        this.headers = new LinkedHashMap<>();
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void status(HttpResponseStatus httpResponseStatus) {
        this.responseLine.status(httpResponseStatus);
    }

    public void resource(String resource) {
        this.resource = resource;
    }

    public void flush() throws IOException {
        dataOutputStream.flush();
    }

    public void write() throws IOException {
        if (responseLine.isStatusEmpty()) {
            throw new HttpFormatException();
        }

        dataOutputStream.writeBytes(responseLine.asLine());
        if (responseLine.isOk()) {
            ok();
            return;
        }

        attachHeaderToResponse();
    }

    private void ok() throws IOException {
        URL url = HttpResponse.class.getClassLoader().getResource("static" + resource);

        if (url == null) {
            dataOutputStream.flush();
            status(HttpResponseStatus.NOT_FOUND);
            return;
        }

        Path path = new File(url.getPath()).toPath();
        byte[] body = Files.readAllBytes(path);
        String extension = resource.substring(resource.lastIndexOf("."));
        headers.put(CONTENT_TYPE, ContentType.findContentType(extension).getType());
        headers.put(CONTENT_LENGTH, body.length + "");

        ok(body);
    }

    private void ok(byte[] body) throws IOException {
        attachHeaderToResponse();
        attachBodyToResponse(body);
    }

    public void location(String location) {
        headers.put(LOCATION, location);
    }

    private void attachHeaderToResponse() throws IOException {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            dataOutputStream.writeBytes(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }
        dataOutputStream.writeBytes("\r\n");

    }

    private void attachBodyToResponse(byte[] body) throws IOException {
        dataOutputStream.write(body, 0, body.length);
    }
}
