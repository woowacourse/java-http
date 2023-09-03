package org.apache.coyote.httprequest.header;

import org.apache.coyote.httprequest.exception.InvalidCacheControlHeader;

import java.util.Arrays;

public enum CacheControlHeader implements RequestHeader {
    NO_CACHE("no-cache");

    private final String option;

    CacheControlHeader(final String option) {
        this.option = option;
    }

    public static CacheControlHeader from(final String option) {
        return Arrays.stream(values())
                .filter(cacheControlHeader -> cacheControlHeader.option.equals(option))
                .findFirst()
                .orElseThrow(InvalidCacheControlHeader::new);
    }

    @Override
    public String getValue() {
        return null;
    }
}
