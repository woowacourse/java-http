package nextstep.jwp.http.response;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;

public class  HttpResponse {
    private DataOutputStream dataOutputStream = null;
    private Map<String, String> headers = new LinkedHashMap<>();

    public HttpResponse(OutputStream outputStream) {
        dataOutputStream = new DataOutputStream(outputStream);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void ok(String resourcePath) {
        URL resource = HttpResponse.class.getClassLoader().getResource("static" + resourcePath);

        if (resource == null) {
            notFound();
            return;
        }

        try {
            Path path = new File(resource.getPath()).toPath();
            byte[] body = Files.readAllBytes(path);
            if (resourcePath.endsWith(".css")) {
                headers.put(CONTENT_TYPE, "text/css");
            } else if (resourcePath.endsWith(".js")) {
                headers.put(CONTENT_TYPE, "application/javascript");
            } else {
                headers.put(CONTENT_TYPE, "text/html;charset=utf-8");
            }
            headers.put(CONTENT_LENGTH, body.length + "");
            ok(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ok(byte[] body) {
        try {
            dataOutputStream.writeBytes("HTTP/1.1 200 OK \r\n");
            attachHeaderToResponse();
            attachBodyToResponse(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void redirect(String redirectUrl) {
        try {
            dataOutputStream.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dataOutputStream.writeBytes("Location: " + redirectUrl + " \r\n");
            dataOutputStream.writeBytes("\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notFound() {
        try {
            dataOutputStream.writeBytes("HTTP/1.1 404 Not Found \r\n");
            dataOutputStream.writeBytes("\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void attachHeaderToResponse() {
        try {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                dataOutputStream.writeBytes(entry.getKey() + ": " + entry.getValue() + " \r\n");
            }
            dataOutputStream.writeBytes("\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void attachBodyToResponse(byte[] body) {
        try {
            dataOutputStream.write(body, 0, body.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
