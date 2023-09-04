# 톰캣 구현하기

## 요구사항

- [x] 요청 경로에 따른 올바른 응답을 한다.
    - [x] 해당 경로에 대한 세부 처리가 있는 경우
        - [x] 지정된 응답이 있는 경우
            - [x] 정적 파일
            - [x] Query String 에 대한 처리

- [x] 정적 파일에 대한 요청인 경우
    - [x] 적절한 Content-Type 을 반환한다.
        - [x] 확장자를 사용
        - [x] Accept 헤더를 사용

- [x] 요청 메시지를 파싱하여 저장한다.
    - [x] Start-line
    - [x] Headers
    - [x] Body

- [x] 응답 메시지를 생성한다.
    - [x] Status Line
    - [x] Headers
    - [x] Body

---

## PathRequestHandler

- 경로에 따른 처리를 캐싱한다.
    - 지정한 Body 를 담아 응답한다.
    - 지정한 Content-Type 헤더와 함께 응답한다.
    - 지정한 Body 의 크기 만큼의 Content-Length 헤더와 함께 응답한다.
    - 정적 파일에 한 처리가 필요한 경우 StaticResourceHandler 에게 처리를 위임한다.

## StaticResourceHandler

- 정적 파일을 Body 에 담아 응답한다.
    - 정적 파일에 맞는 Content-Type 헤더와 함께 응답한다.
    - 정적 파일에 크기 만큼의 Content-Length 헤더와 함께 응답한다.

