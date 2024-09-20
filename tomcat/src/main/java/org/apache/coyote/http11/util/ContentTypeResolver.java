package org.apache.coyote.http11.util;

import org.apache.coyote.http11.constants.ContentType;

public class ContentTypeResolver {

    public static ContentType resolve(final String resourceUri, final String acceptLine) {
        final int extensionStartIndex = resourceUri.lastIndexOf(".") + 1;
        final String fileExtension = resourceUri.substring(extensionStartIndex);

        if (acceptLine != null && acceptLine.contains(fileExtension)) {
            return ContentType.toContentType(fileExtension);
        }
        return ContentType.ASTERISK;
    }
}
