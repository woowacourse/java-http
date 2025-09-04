package org.apache.coyote.http11.service;

import org.apache.coyote.http11.parser.ContentParseResult;

import java.util.Map;

public interface HttpService {

    ContentParseResult doRequest(Map<String, String> query);
}
