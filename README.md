# 톰캣 구현하기

## 기능 목록 Step1,2

- [x] GET /index.html로 접속하면 index.html 파일을 읽어 클라이언트에 응답한다.
- [x] GET /css/styles.css로 접속하면 static/css/styles.css 파일을 읽어 클라이언트에 응답한다.
    - [x] css 외의 js, html 파일도 위와 같이 처리한다.
- [x] GET /login?userId=gugu&password=password로 접속하면 로그인에 성공한다.
- [x] POST /register 로 접속하면 회원가입을 할 수 있다.
    - [x] 회원가입 성공시 302 코드와 함께 /login.html로 이동한다.
    - [x] 회원가입 실패시 302 코드와 함께 /401.html로 이동한다.
- [x] POST /login으로 변경
    - 로그인 성공시 302 코드와 함께 /index.html로 이동한다.
    - 로그인 성공시 JSESSIONID 쿠키를 생성한다.
    - 로그인 실패시 302 코드와 함께 /401.html로 이동한다.
- [x] 접근할 수 없는 경로로 접속하면 404.html로 이동한다.
- [x] JSESSIONID 쿠키가 존재하는 경우 login.html로 이동시 index.html로 이동한다.

### 기능 목록 Step3,4

- [x] Controller와 AbstractController를 구현
- [x] 각 요청별로 Controller를 생성
- [x] RequestMapping을 통해 요청에 맞는 Controller를 찾아서 실행
- [x] 기존 코드를 RequestMapping을 통해 실행하도록 수정
- [x] Connectors에서 ExcutorService 사용
- [x] SessionManger에서 ConcurrentHashMap 사용