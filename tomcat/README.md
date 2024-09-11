# 기능 목록

## 1. HTTP 서버 구현

- [x] GET /index.html 응답하기
    - 인덱스 페이지(http://localhost:8080/index.html)에 접근할 수 있다.
    - Http11ProcessorTest의 모든 테스트 통과한다.
- [x] CSS 지원하기
    - 사용자가 페이지를 열었을 때 CSS 파일이 호출된다.
- [x] Query String 파싱
    - [x] ~~http://localhost:8080/login?account=gugu&password=password으로 접속하면 로그인 페이지(login.html)가 보여진다.~~
        - http://localhost:8080/login 접속시 로그인 페이지로 이동한다.
    - [x] ~~로그인 페이지 접속 시, Query String을 파싱해서 아이디, 비밀번호가 일치하면 콘솔창에 로그로 회원을 조회한 결과가 나온다.~~

## 2. 로그인 구현

- [x] 로그인 여부에 따른 페이지 이동
    - [x] 성공 : 응답 헤더에 http status code를 302로 반환하고 `/index.html`로 리다이렉트 한다.
    - [x] 실패 : `401.html`로 리다이렉트 한다.
- [x] POST 방식으로 회원가입
    - [x] http://localhost:8080/register으로 접속하면 회원가입 페이지(register.html)가 보여진다.(GET)
    - [x] 회원가입 버튼을 눌러 회원가입할 수 있다.(POST)
        - 회원가입 완료 시, `index.html`로 리다이렉트한다.(GET)
    - [x] 로그인 버튼을 눌러 로그인할 수 있다.(POST)
        - 로그인 완료 시, `index.html`로 리다이렉트한다.(GET)
- [x] Cookie에 JSESSIONID 값 저장하기
    - Cookie 클래스를 추가한다.
    - HTTP Request Header의 Cookie에 JSESSIONID가 없으면 Response Header에 Set-Cookie를 반환한다.
- [x] Session 구현하기
    - JSESSIONID의 값으로 로그인 여부를 체크한다.
    - [x] 로그인 성공 시 Session 객체의 값으로 User 객체를 저장한다.
    - [x] 로그인된 상태에서 `/login` 페이지에 접근하면, `index.html` 페이지로 리다이렉트 한다.

## 3. 리팩터링

- [x] HttpRequest 클래스 구현하기
    - RequestLine 클래스 추가
- [ ] HttpResponse 클래스 구현하기
- [ ] Controller 인터페이스 추가하기
    - RequestMapping 클래스 구현

---

### 생각해보기 🤔

> index.html 페이지만 접근했는데 CSS 같은 정적 파일들은 어떻게 호출된걸까?

index.html 문서에 `<link href="css/styles.css" rel="stylesheet" />` 통해 CSS 정적 파일이 호출되고 있다.
