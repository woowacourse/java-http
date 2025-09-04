package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.dto.HttpRequest;

public class HttpResponseHandler {

    public void process(HttpRequest httpRequest, OutputStream outputStream) throws IOException {

        if (httpRequest.path().equals("/index.html")) {
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            var response =  String.join("\r\n",
                    httpRequest.version() + " " + "200" + " OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + body.getBytes().length + " ",
                    "",
                    body);
            outputStream.write(response.getBytes());
            outputStream.flush();
        }
    }
}
