package org.apache.util.parser;

import java.util.Map;

public interface Parser {

    Map<String, String> parse(String body);
}
