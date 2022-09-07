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
- [ ] Cookie에 JSESSIONID 값 저장하기
- [ ] Session 구현하기

리팩터링

- [x] 패키지 구조에 맞게 application 영역과 framework 영역 분리하기
- [x] Http11Response 내부와 테스트에서만 사용되는 getOkResponse() 메서드 private으로 수정  
      -> StatusCode 클래스에서 사용되어 public으로 유지
- [ ] response header, response body를 클래스로 분리
- [ ] request header, request body를 클래스로 분리
- [ ] 로그인 페이지에서 패스워드를 입력하지 않으면 발생하는 예외 수정
- [ ] 회원 정보 필드가 비어있는 경우 발생하는 예외 수정
- [x] 리다이렉트 시 location을 할당하여 해당 url로 리다이렉트 되도록 수정

</details>
