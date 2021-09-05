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
- [ ] 요청에 쿠키가 들어오면 HttpRequest 에서 쿠키를 얻어내는 기능 구현하기
  - [x] HttpCookies 객체 만들기
  - [x] RequestHeader 에 쿠키를 추출하는 메서드 만들기
  
## 🔨 리팩터링 목록
- [ ] key-value 구조 맵으로 파싱할 일이 많아서 유틸에 만들어서 사용하고 기존 코드 변경
- [ ] 원랜 주 생성자만 열어두고 싶었지만, 비어있는 객체를 캐싱한 경우 주 생성자를 닫을 수 밖에 없어 일관성을 위해 모든 생성자를 닫기로 함
- [ ] 요청에 쓰는 헤더 상수는 RequestHeader 쪽에 응답에 쓰는 필드는 ResponseHeader 쪽으로 옮김 
