# 톰캣 구현하기

<details>
<summary>1단계 - HTTP 서버 구현하기</summary>

구현 기능 목록

- [x] GET /index.html 응답하기
- [x] .css, .js 지원하기
- [x] Query String 파싱하기

리팩터링

- [x] Http11Request를 만들어 InputStream으로부터 request를 가져오는 책임 분리
- [x] Url를 enum으로 만들어 Processor 내의 분기 처리 제거
- [x] Http11Response를 만들어 response를 만드는 책임 분리
    - [x] outputStream.write() 부분까지 책임 분리
- [x] GET 이외의 요청에 대한 처리
- [x] 커스텀 예외를 만들어 상황에 보다 적합한 예외 반환
- [x] 로그인시 존재하는 유저인지 확인하는 로직 처리 고민하기
    - [x] queryString을 관리하는 책임 분리
    -

</details>

<details>
<summary>2단계 - 로그인 구현하기</summary>

구현 기능 목록

- [x] HTTP Status Code 302
- [x] POST 방식으로 회원가입
- [x] Cookie에 JSESSIONID 값 저장하기
- [x] Session 구현하기

리팩터링

- [x] 패키지 구조에 맞게 application 영역과 framework 영역 분리하기
- [x] Http11Response 내부와 테스트에서만 사용되는 getOkResponse() 메서드 private으로 수정  
  -> StatusCode 클래스에서 사용되어 public으로 유지
- [x] response header, response body를 클래스로 분리
- [x] request header, request body를 클래스로 분리
- [x] 로그인 페이지에서 패스워드를 입력하지 않으면 발생하는 예외 수정
- [x] 회원 정보 필드가 비어있는 경우 발생하는 예외 수정
- [x] 리다이렉트 시 location을 할당하여 해당 url로 리다이렉트 되도록 수정
- [x] Url 내부의 Function 로직을 Handler 클래스로 분리
- [x] 로그인 요청을 POST 로 메서드 변경
- [x] null을 사용하는 부분 최대한 Optional 로 수정
- [x] response 에서 setCookie 부분을 JsessionId 에 의존적이지 않게 수정
- [x] 매직 넘버 상수로 분리하기

</details>

<details>
<summary>3단계 - 리팩터링</summary>

구현 기능 목록

- [x] HttpRequest 클래스 구현하기
- [x] HttpResponse 클래스 구현하기
- [x] Controller 인터페이스 추가하기

리팩터링

- [ ] 테스트 코드 추가하기
- [x] apache 패키지에서 nextstep 패키지를 참조하지 않도록 수정
- [x] response header를 map 형태로 수정

</details>

<details>
<summary>4단계 - 동시성 확장하기</summary>

구현 기능 목록

- [x] Executors로 Thread Pool 적용
- [x] 동시성 컬렉션 사용하기

리팩터링

- [ ] getOrDefault() 사용하기
- [ ] 예외 상황에 대한 리다이렉트를 예외를 발생시킨 뒤 예외에 해당하는 페이지로 이동하도록 수정
- [ ] 어떨 때 log를 남기면 좋을지 고민해보고 logger 추가하기

</details>
