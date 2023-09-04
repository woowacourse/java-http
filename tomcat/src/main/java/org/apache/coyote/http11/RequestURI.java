package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
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

    private String parsePath(String uri) {
        int index = uri.indexOf("?");
        if (index == -1) {
            return uri;
        }

        return uri.substring(0, index);
    }

    private QueryParameter parseQueryString(String uri) {
        int index = uri.indexOf("?");
        if (index == -1) {
            return QueryParameter.EMPTY;
        }

        return new QueryParameter(uri.substring(index + 1));
    }

    public String readFile() {
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 읽던 중 에러가 발생했습니다.");
        }
    }

    public int getFileLength() throws IOException {
        return new String(Files.readAllBytes(file.toPath())).getBytes().length;
    }

    public boolean isExistFile() {
        return this.file == null;
    }

    public String getPath() {
        return this.path;
    }

    public Map<String, String> getQueryParameter() {
        return queryParameter.getQueryParameter();
    }
}
