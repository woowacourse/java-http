package nextstep.jwp.request;

import java.net.URL;

public class RequestUrl {

    private final URL url;

    public RequestUrl(ClassLoader classLoader, FileName fileName) {
        this(classLoader.getResource(fileName.getName()));
    }

    public RequestUrl(URL url) {
        this.url = url;
    }

    public RequestFile toRequestFile() {
        return new RequestFile(url);
    }

    public URL getUrl() {
        return url;
    }
}
