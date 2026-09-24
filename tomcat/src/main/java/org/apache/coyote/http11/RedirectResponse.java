package org.apache.coyote.http11;

public record RedirectResponse(HttpStatusCode httpStatusCode, String redirectURL) {
}
