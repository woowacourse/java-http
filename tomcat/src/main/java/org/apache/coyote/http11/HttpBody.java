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

    private final String body;
    private final Map<String, String> bodyToKeyValue;

    public HttpBody(final String body) {
        this.body = body;
        this.bodyToKeyValue = bodyToMap(body);
    }

    private Map<String, String> bodyToMap(final String body) {
        if (body.contains("&") && body.contains("=")) {
            return PairConverter.toMap(body, "&", "=");
        }
        return Map.of();
    }

    public static HttpBody createByUrl(final String url) throws IOException {
        return new HttpBody(getContent(url));
    }

    public String getValue(final String key) {
        if (bodyToKeyValue.containsKey(key)) {
            return bodyToKeyValue.get(key);
        }
        throw new ElementNotFoundException();
    }

    private static String getContent(final String url) throws FileNotFoundException, IOException {
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
        if (body == null) {
            return "";
        }
        return this.body;
    }
}
