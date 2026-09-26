package org.apache.coyote.http11;

import org.apache.coyote.http11.enums.HttpMethod;

public record Route(HttpMethod httpMethod, String path) {
}
