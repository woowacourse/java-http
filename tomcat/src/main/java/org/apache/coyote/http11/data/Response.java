package org.apache.coyote.http11.data;

import java.util.Map;

public record Response(
            int statusCode,
            Map<String, String> responseHeaderMap,
            String responseBody) {

        public static Response noContent() {
            return new Response(204, Map.of(), "");
        }

        public static Response ok() {
            return new Response(200, Map.of(), "");
        }

        public static Response ok(
                final Map<String, String> responseHeaderMap,
                final String responseBody) {

            return new Response(200, responseHeaderMap, responseBody);
        }

        public static Response notFound() {
            return new Response(404, Map.of(), "Not Found");
        }
    }