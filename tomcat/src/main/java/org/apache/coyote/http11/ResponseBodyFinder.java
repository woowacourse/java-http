package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class ResponseBodyFinder {

    public boolean isBodyExist(final String targetUrl) {
        return UrlsWithBodyEnum.contains(targetUrl) || Objects.nonNull(getResource(targetUrl));
    }

    private URL getResource(final String targetUrl) {
        return getClass().getClassLoader().getResource("static" + targetUrl);
    }

    public byte[] getBody(final String targetUrl) throws IOException {
        if (UrlsWithBodyEnum.contains(targetUrl)) {
            return UrlsWithBodyEnum.getBody(targetUrl);
        }

        final URL resource = getResource(targetUrl);

        if (Objects.isNull(resource)) {
            return new byte[0];
        }

        return Files.readAllBytes(new File(resource.getFile()).toPath());
    }

    public String getContentType(final String targetUrl) {
        if (StaticResourceContentType.findable(targetUrl)) {
            return StaticResourceContentType.find(targetUrl);
        }
        return "";
    }

    public String getContentTypeWithCharSet(final String targetUrl, final String charset) {
        if (StaticResourceContentType.findable(targetUrl)) {
            return StaticResourceContentType.findWithAdditionalCharset(targetUrl,
                Charset.forName(charset));
        }
        return "";
    }

    enum UrlsWithBodyEnum {
        ROOT("/", "Hello world!".getBytes());

        private String url;
        private byte[] body;

        UrlsWithBodyEnum(final String url, final byte[] body) {
            this.url = url;
            this.body = body;
        }

        public static boolean contains(final String targetUrl) {
            return Arrays.stream(values())
                .anyMatch(urlWithBody -> urlWithBody.url.equals(targetUrl));
        }

        public static byte[] getBody(final String targetUrl) {
            final Optional<UrlsWithBodyEnum> findUrl = Arrays.stream(values())
                .filter(urlWithBody -> urlWithBody.url.equals(targetUrl))
                .findFirst();

            if (findUrl.isEmpty()) {
                return new byte[0];
            }

            return findUrl.get().body;
        }
    }
}





