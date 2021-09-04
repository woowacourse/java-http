package nextstep.jwp.http.response;

import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private DataOutputStream dataOutputStream;
    private Map<String, String> headers = new LinkedHashMap<>();
    private HttpResponseStatus httpResponseStatus;
    private String resource;

    public HttpResponse(OutputStream outputStream) {
        dataOutputStream = new DataOutputStream(outputStream);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void status(HttpResponseStatus httpResponseStatus) {
        this.httpResponseStatus = httpResponseStatus;
    }

    public void resource(String resource) {
        System.out.println("resource : " + resource);
        this.resource = resource;
    }

    public void flush() throws IOException {
        dataOutputStream.flush();
    }

    public void write() throws IOException {
        if (httpResponseStatus == null) {
            throw new RuntimeException();
        }

        dataOutputStream.writeBytes(HttpVersion.HTTP_VERSION_1_1 + " " + httpResponseStatus.getLine() + "\r\n");
        if (HttpResponseStatus.OK.equals(httpResponseStatus)) {
            ok();
            return;
        }

        attachHeaderToResponse();
    }

    private void ok() {
        URL url = HttpResponse.class.getClassLoader().getResource("static" + resource);

        if (resource == null) {
            throw new RuntimeException();
        }

        try {
            Path path = new File(url.getPath()).toPath();
            byte[] body = Files.readAllBytes(path);
            String extension = resource.substring(resource.lastIndexOf("."));
            headers.put(CONTENT_TYPE, ContentType.findContentType(extension).getType());
            headers.put(CONTENT_LENGTH, body.length + "");
            ok(body);
        } catch (IOException e) {
            log.error("Exception ok", e);
        }
    }

    private void ok(byte[] body) {
        attachHeaderToResponse();
        attachBodyToResponse(body);
    }

    public void location(String location) {
        headers.put(LOCATION, location);
    }

    private void attachHeaderToResponse() {
        try {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                dataOutputStream.writeBytes(entry.getKey() + ": " + entry.getValue() + "\r\n");
            }
            dataOutputStream.writeBytes("\r\n");
        } catch (IOException e) {
            log.error("Exception attachHeaderToResponse", e);
        }

    }

    private void attachBodyToResponse(byte[] body) {
        try {
            dataOutputStream.write(body, 0, body.length);
        } catch (IOException e) {
            log.error("Exception attachBodyToResponse", e);
        }
    }
}
