package nextstep.jwp.http;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class ViewResolver {


    public String findResource(String uri) throws IOException {
        if (!uri.contains("html")) {
            uri += ".html";
        }
        if(!uri.contains("/")){
            uri = "/" + uri;
        }

        final URL resource = getClass().getClassLoader().getResource("static" + uri);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
