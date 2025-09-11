package org.apache.coyote.http11.message.parser;

import java.io.BufferedReader;
import java.io.IOException;

public interface Parser<T> {
    T parse(BufferedReader reader) throws IOException;
}
