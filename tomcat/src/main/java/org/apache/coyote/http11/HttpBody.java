package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import org.apache.coyote.http11.exception.ElementNotFoundException;
import org.apache.coyote.http11.exception.FileNotFoundException;
import org.apache.coyote.http11.utils.PairConverter;

public class HttpBody {

    private static final String RESOURCE_PATH = "static";
    private static final String DEFAULT_EXTENSION = "html";
    private static final String EXTENSION_DELIMITER = ".";

    private String body;

    public HttpBody(final String body) {
        this.body = body;
    }

    public static HttpBody createByUrl(final String url) throws IOException {
        return new HttpBody(getContent(url));
    }

    public String getValue(final String key) {
        final Map<String, String> bodyMap = PairConverter.toMap(body, "&", "=");
        if (bodyMap.containsKey(key)) {
            return bodyMap.get(key);
        }
        throw new ElementNotFoundException();
    }

    protected static String getContent(final String url) throws FileNotFoundException, IOException {
        final URL resource = HttpBody.class
                .getClassLoader()
                .getResource(RESOURCE_PATH + url + getExtension(url));

        if (resource == null) {
            throw new FileNotFoundException();
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private static String getExtension(final String path) {
        if (path.contains(EXTENSION_DELIMITER)) {
            return "";
        }
        return EXTENSION_DELIMITER + DEFAULT_EXTENSION;
    }

    public String getBody() {
        if (body == null)  {
            return "";
        }
        return this.body;
    }
}
