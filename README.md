# 톰캣 구현하기

## 1단계 - HTTP 서버 구현하기
- [x] 주어진 코드 수정하기
  - [x] 주어진 요청이 있는지 판단하는 HandlerMapping 구현
  - [x] 요청에 따른 응답을 생성하는 Handler 구현
- [x] GET /index.html 응답하기
  - [x] string을 반환하는 요청과 file을 반환하는 요청을 분리
- [x] CSS 및 JS 지원하기
  - [x] CSS 및 JS 요청에 대한 HandlerMapping 요소 추가
- [x] Query String 파싱하기
  - [x] 로그인 요청에 대한 HandlerMapping 요소 추가
  - [x] QueryString 객체 생성
  - [x] login에서 쿼리 요청시 존재하는 사용자이면 로그 출력
## 2단계 - 로그인 구현하기
- [x] 로그인 요청
  - [x] 있는 회원이면 index.html로 리다이렉트 하기
  - [x] 없는 회원이면 401.html로 리다이렉트 하기
- [x] POST 방식으로 회원가입
  - [x] 회원가입 페이지로 응답하는 요청 만들기
  - [x] 회원가입시 회원 가입 정보를 메모리에 저장하기(회원가입 버튼을 눌렀을 때)
  - [x] 로그인 방식에 post 방식 추가(로그인 버튼을 눌렀을 때)
- [x] Cookie에 JSESSIONID값 저장하기
  - [x] 이미 로그인된 상황이라면 로그인 화면 접근시 메인화면으로 리다이랙트
  - [x] 로그인시 쿠키 생성해주기
- [x] Session 구현하기
  - [x] 로그인시 세션저장하기
## 3단계
- [x] 전반적인 코드 리팩토링 하기
  - [x] HttpRequest 및 HttpResponse 도입하기
  - [x] controller 도입하기
  - [x] 파일 읽어오는 기능 util 클래스로 분리
## 4단계
- [x] Executors로 Thread Pool 적용
- [x] 동시성 컬렉션 사용하기

## 리팩토링
- [x] 코드 가독성 향상 시키기
  - [x] 불필요한 if문 제거하기
  - [x] 상속시 중복되는 상수는 부모에 두고 사용하기
- [x] 필드 접근제어자 수정하기
- [ ] 회원가입 시도 시 잘못된 값이 넘어오면 예외를 주기
- [x] POST와 GET이 아닌 HttpMethod가 요청으로 들어면 405 응답하기 (현재 지원하는 메소드는 POST와 GET만 있습니다.)
- [x] 유티클래스의 경우 외부에서 객체 생성을 못하게 private 생성자 추가하기
