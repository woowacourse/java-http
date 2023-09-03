package org.apache.coyote.request;

public class Accept {

    private static final String QUALITY_PREFIX = "q=";

    private final String acceptType;
    private final double quality;

    public Accept(final String acceptType, final double quality) {
        this.acceptType = acceptType;
        this.quality = quality;
    }

    public static Accept from(final String[] accepts) {
        if (accepts.length == 1) {
            return new Accept(accepts[0], 0);
        } else {
            final String quality = accepts[1].replace(QUALITY_PREFIX, "");
            return new Accept(accepts[0], Double.valueOf(quality));
        }
    }

    public String getAcceptType() {
        return acceptType;
    }

    public double getQuality() {
        return quality;
    }
}
