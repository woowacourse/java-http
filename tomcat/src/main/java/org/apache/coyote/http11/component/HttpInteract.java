package org.apache.coyote.http11.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface HttpInteract {
    String LINE_DELIMITER = "\r\n";

    default String extractHeader(final List<String> lines) {
        final var headerTexts = Collections.synchronizedList(new ArrayList<String>());
        for (var i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) {
                break;
            }
            headerTexts.add(lines.get(i));
        }
        return String.join("\r\n", headerTexts).replaceAll(" ", "");
    }
}
