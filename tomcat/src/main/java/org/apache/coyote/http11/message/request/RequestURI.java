package org.apache.coyote.http11.message.request;

import java.io.File;
import java.net.URL;
import java.util.Map;

public class RequestURI {
    private static final String ROOT_FOLDER = "static";
    private final String path;
    private final QueryParameter queryParameter;
    private final File file;

    public RequestURI(final String path, final QueryParameter queryParameter, final File file) {
        this.path = path;
        this.queryParameter = queryParameter;
        this.file = file;
    }

    public RequestURI(final String uri) {
        this.path = parsePath(uri);
        this.queryParameter = parseQueryString(uri);

        final URL resource = getClass().getClassLoader().getResource(ROOT_FOLDER + uri);
        if (resource == null) {
            this.file = null;
            return;
        }
        final File findedFile = new File(resource.getPath());
        if (findedFile.isFile()) {
            this.file = findedFile;
            return;
        }
        this.file = null;
    }

    private String parsePath(final String uri) {
        final int index = uri.indexOf("?");
        if (index == -1) {
            return uri;
        }

        return uri.substring(0, index);
    }

    private QueryParameter parseQueryString(final String uri) {
        final int index = uri.indexOf("?");
        if (index == -1) {
            return QueryParameter.EMPTY;
        }

        return new QueryParameter(uri.substring(index + 1));
    }

    public boolean isExistFile() {
        return this.file.isFile();
    }

    public String getPath() {
        return this.path;
    }

    public Map<String, String> getQueryParameter() {
        return queryParameter.getParameters();
    }
}
