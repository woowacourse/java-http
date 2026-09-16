package org.apache.coyote.http;

public record TextBody(String raw) implements RequestBody {

    static final TextBody EMPTY = new TextBody("");
}
