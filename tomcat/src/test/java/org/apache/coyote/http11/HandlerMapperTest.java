package org.apache.coyote.http11;

class HandlerMapperTest {

//    public static class TestController implements Controller {
//        @RequestMapping(value = "/", method = RequestMethod.GET)
//        public ResponseEntity myMethod(HttpRequest httpRequest) {
//            return new ResponseEntity(HttpStatus.OK, "hello", determineContentType(path));
//        }
//    }
//
//    @Test
//    void createMapperByController() {
//        // given & when
//        TestController controller = new TestController();
//        // then
//        assertThatNoException().isThrownBy(() -> HandlerMapper.from(List.of(controller)));
//    }
//
//    @Test
//    void mapRequestToHandler() {
//        // given
//        TestController controller = new TestController();
//        HandlerMapper mapper = HandlerMapper.from(List.of(controller));
//
//        String http = String.join("\n",
//                "GET / HTTP/1.1",
//                "Host: localhost:8080 ",
//                "Connection: keep-alive ",
//                "",
//                ""
//        );
//        // when
//
//        ResponseEntity responseEntity = mapper.mapToHandle(
//                HttpRequest.parse(new ByteArrayInputStream(http.getBytes())));
//
//        // then
//        Assertions.assertAll(
//                () -> assertThat(responseEntity.getStatus()).isEqualTo(HttpStatus.OK),
//                () -> assertThat(responseEntity.getBody()).isEqualTo("hello")
//        );
//    }
//
//    @Test
//    void mapToStaticFile() {
//        // given
//        TestController controller = new TestController();
//        HandlerMapper mapper = HandlerMapper.from(List.of(controller));
//
//        String http = String.join("\n",
//                "GET /index.html HTTP/1.1",
//                "Host: localhost:8080 ",
//                "Connection: keep-alive ",
//                "",
//                ""
//        );
//        // when
//
//        ResponseEntity responseEntity = mapper.mapToHandle(
//                HttpRequest.parse(new ByteArrayInputStream(http.getBytes())));
//
//        // then
//        assertThat(responseEntity.getStatus()).isEqualTo(HttpStatus.OK);
//    }
//
//    @Test
//    void mapToNotFound() {
//        // given
//        TestController controller = new TestController();
//        HandlerMapper mapper = HandlerMapper.from(List.of(controller));
//
//        String http = String.join("\n",
//                "GET /not-found HTTP/1.1",
//                "Host: localhost:8080 ",
//                "Connection: keep-alive ",
//                "",
//                ""
//        );
//        // when
//
//        ResponseEntity responseEntity = mapper.mapToHandle(
//                HttpRequest.parse(new ByteArrayInputStream(http.getBytes())));
//
//        // then
//        assertThat(responseEntity.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
}
