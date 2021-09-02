package nextstep.jwp.response;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {
    private static final String CONTENT_TYPE = "Content-Type";
    
    private final OutputStream outputStream;
    private final Map<String, String> headers = new HashMap<>();

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void forward(String url) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static" + url);
        final List<String> lines = Files.readAllLines(new File(resource.getPath()).toPath());

        if (url.endsWith(".js")) {
            headers.put(CONTENT_TYPE, "text/javascript");
        } else if (url.endsWith(".css")) {
            headers.put(CONTENT_TYPE, "text/css");
        } else {
            headers.put(CONTENT_TYPE, "text/html;charset=utf-8");
        }

        String file = lines.stream()
                           .map(String::valueOf)
                           .collect(Collectors.joining());

        headers.put("Content-Length", Integer.toString(file.getBytes().length));

        create200StatusLine();
        createHeaders();
        createBody(file);
    }

    private void create200StatusLine() throws IOException {
        outputStream.write("HTTP/1.1 200 OK \r\n".getBytes());
    }

    private void createHeaders() throws IOException {
        for (String key : headers.keySet()) {
            outputStream.write((key + ": " + headers.get(key) + " \r\n").getBytes());
        }
    }

    private void createBody(String file) throws IOException {
        outputStream.write("\r\n".getBytes());
        outputStream.write(file.getBytes());
        outputStream.flush();
    }

    public void redirect(String url) throws IOException {
        outputStream.write("HTTP/1.1 302 Found \r\n".getBytes());

        final URL resource = getClass().getClassLoader().getResource("static" + url);
        final List<String> lines = Files.readAllLines(new File(resource.getPath()).toPath());
        String file = lines.stream()
                           .map(String::valueOf)
                           .collect(Collectors.joining());

        headers.put("Location", " " + url + " ");
        headers.put("Content-Length", Integer.toString(file.getBytes().length));
        createHeaders();
        createBody(file);
    }
}
