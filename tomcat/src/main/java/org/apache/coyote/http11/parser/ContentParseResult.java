package org.apache.coyote.http11.parser;

public class ContentParseResult {

    private final byte[] parsedContent;
    private final String additionalResponse;

    public ContentParseResult(byte[] parsedContent, String additionalResponse) {
        this.parsedContent = parsedContent;
        this.additionalResponse = additionalResponse;
    }

    public byte[] getParseContent() {
        return parsedContent;
    }

    public String getAdditionalResponse() {
        return additionalResponse;
    }
}
