package org.apache.coyote.common;

import org.apache.coyote.request.QueryString;

public class PathUrl {
    private final String path;
    private final QueryString queryString;
    private final FileType fileType;

    public PathUrl(
            final String path,
            final QueryString queryString,
            final FileType fileType
    ) {
        this.path = path;
        this.queryString = queryString;
        this.fileType = fileType;
    }

    public static PathUrl from(final String url) {
        final String path = getPath(url);
        final FileType fileType = FileType.get(path);
        final QueryString query = QueryString.from(url.substring(path.length()));
        final String pathWithoutExtension = path.split("\\.")[0];
        return new PathUrl(pathWithoutExtension, query, fileType);
    }

    private static String getPath(final String url) {
        if(url.isBlank()){
            return "/";
        }
        return url.split("\\?")[0];
    }

    public boolean hasExtension() {
        return fileType.hasExtension();
    }

    public String getFileType() {
        return fileType.getExtension();
    }

    public String getContentType(){
        return fileType.getContentType();
    }

    public String getPath(){
        return path;
    }

    public String getQueryValueBy(final String key) {
        return queryString.getQueryValueBy(key);
    }

    public boolean hasQueryString() {
        return queryString.isNotEmpty();
    }

    @Override
    public String toString() {
        return path + fileType + queryString;
    }
}
