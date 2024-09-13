package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.VersionOfProtocol;

public record RequestLine(Method method, Path path, VersionOfProtocol versionOfProtocol) {

}
