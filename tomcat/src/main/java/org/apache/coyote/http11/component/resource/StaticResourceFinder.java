package org.apache.coyote.http11.component.resource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.component.common.MimeType;
import org.apache.coyote.http11.component.common.body.TextTypeBody;
import org.apache.coyote.http11.component.response.HttpResponse;
import org.apache.coyote.http11.component.response.ResponseHeader;
import org.apache.coyote.http11.component.response.ResponseLine;

public class StaticResourceFinder {

    private final URL resourcePath;

    public StaticResourceFinder(final String resourceName) {
        this.resourcePath = getClass().getClassLoader().getResource("static/" + resourceName);
    }

    public static HttpResponse render(final String resourceName) {
        final var resourceFinder = new StaticResourceFinder(resourceName);
        final var ok = ResponseLine.OK;
        final var responseHeader = new ResponseHeader();
        responseHeader.put("Content-Length", String.valueOf(resourceFinder.getBytes().length));
        responseHeader.put("Content-Type", MimeType.find(resourceName) + ";charset=utf-8");
        final var textTypeBody = new TextTypeBody(new String(resourceFinder.getBytes()));
        return new HttpResponse(ok, responseHeader, textTypeBody);
    }

    public static HttpResponse renderRedirect(final String location) {
        final var found = ResponseLine.FOUND;
        final var responseHeader = new ResponseHeader();
        responseHeader.put("Location", location);
        final var textTypeBody = new TextTypeBody("");
        return new HttpResponse(found, responseHeader, textTypeBody);
    }

    private byte[] getBytes() {
        try {
            return Files.readAllBytes(new File(resourcePath.getFile()).toPath());
        } catch (IOException e) {
            throw new IllegalStateException("존재하지 않는 정적 리소스");
        }
    }
}
