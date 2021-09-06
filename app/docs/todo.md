## 🚀 1단계 - HTTP 서버 구현하기
- [x] http request 를 파싱하기
  - [x] StringUtils 만들기
- [x] http response 만들기
- [x] 정적 요청 관련 매핑
  - [x] html 요청 관련 매핑
  - [x] css 요청 관련 매핑
  - [x] js 요청 관련 매핑
- [x] forward 요청 관련 매핑
- [x] redirect 요청 관련 매핑
- [x] 매핑이 없으면 404를 응답한다.
- [x] 인증에 실패하면 401을 응답한다.
- [x] POST 구현
  - [x] 회원가입 구현
  - [x] login POST로 리팩터링
  

## 🍪 3단계 쿠키, 세션 구현하기
- [x] HttpCookies 객체 만들기
- [x] RequestHeader 에 쿠키를 추출하는 메서드 만들기
- [x] HttpSession 클래스 만들기
- [x] HttpSessions 클래스 만들기
- [x] 요청에 쿠키가 들어오면 HttpRequest 에서 세션을 얻어내는 기능 구현하기

## 🔨 리팩터링 목록
- [x] key-value 구조 맵으로 파싱할 일이 많아서 유틸에 만들어서 사용하고 기존 코드 변경
- [x] 원랜 주 생성자를 열어두고 싶었지만, 비어있는 객체를 캐싱한 경우 주 생성자를 닫을 수 밖에 없었음 
  일관성을 위해 팩토리 메서드가 존재하는 클래스는 생성자를 닫음
- [x] 헤더 상수는 RequestHeader 와 ResponseHeader 쪽으로 옮기고 HeaderFields 는 컬렉션의 역할에 집중하도록 함
      구조상 헤더를 읽는 메서드는 RequestHeader 에 헤더에 값을 추가하는 메서드는 ResponseHeader 에 있기 때문에 분리를 생각함
  
## 🐥 로그인 동작 계획
### GET /login 요청
- 세션을 확인하여 유효한 세션이
  - 없으면 /login.html 로 포워딩 
    - 쿠키에 JSESSIONID 가 없는 경우
    - 쿠키에 JSESSIONID 는 있지만 세션 저장소에 없는 경우 - 클라이언트에게 max-age 설정 해줘야 할까?
    - 만료된 세션의 JSESSIONID 를 가지고 있는 경우
  - 있으면 
    - 세션에 User가 있는지 확인하여
      - 있으면 /index.html 로 리다이렉팅한다.
      - 없으면 /login.html 로 포워딩

### POST /login 요청
2. 세션을 가져오는데, 없으면 새로 만든다. 
- 쿠키가 JSESSIONID 를 안 가지고 있는 경우 - 새로 만들어 주고 
- 가지고 있지만 저장소에 없는 경우 - 새로 만들어 주고
- 가지고 있지만 만료된 경우 - 새로 만들어 주고
- 가지고 있고 만료 안 된 경우 - 기존 세션 주는데 액세스 타임 새로고침
3. 얻어 낸 세션에 user 가 있는지 확인한다.

