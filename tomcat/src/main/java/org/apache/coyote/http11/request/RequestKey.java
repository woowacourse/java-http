package org.apache.coyote.http11.request;

import org.apache.coyote.HttpMethod;

public record RequestKey(HttpMethod method, String path) {

}
