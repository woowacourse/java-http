package org.apache.coyote.http;

import java.io.IOException;
import org.apache.catalina.resource.ResourcePathResolver;
import org.apache.catalina.resource.StaticResourceLoader;

public class StaticResourceBody implements ResponseBody {

    private static final StaticResourceLoader loader = new StaticResourceLoader();

    private final byte[] content;
    private final MimeType mimeType;

    public StaticResourceBody(byte[] content, MimeType mimeType) {
        this.content = content;
        this.mimeType = mimeType;
    }

    public static StaticResourceBody from(String path) throws IOException {
        String resolvedPath = ResourcePathResolver.resolve(path);
        return new StaticResourceBody(loader.load(resolvedPath), MimeType.fromPath(resolvedPath));
    }

    @Override
    public String contentType() {
        return mimeType.value();
    }

    @Override
    public byte[] bytes() {
        return content;
    }
}
