package org.apache.http;

public interface HttpResponse {

    String getResponseHttpMessage();

    /**
     *     private String getResponseBodyByURI(final HttpRequest request) throws IOException {
     *         var requestURI = request.getRequestURI();
     *
     *         if (!requestURI.contains(".")) {
     *             return mainController.doService(request);
     *         }
     *
     *         final var resource = getClass().getClassLoader().getResource(String.format("static%s", requestURI));
     *         final var path = new File(resource.getFile()).toPath();
     *
     *         return new String(Files.readAllBytes(path));
     *     }
     *
     *     private String createResponseWithBody(final String responseBody, final String contentType) {
     *         return String.join("\r\n",
     *                 "HTTP/1.1 200 OK ",
     *                 String.format("Content-Type: %s ", contentType),
     *                 "Content-Length: " + responseBody.getBytes().length + " ",
     *                 "",
     *                 responseBody);
     *     }
     */
}
