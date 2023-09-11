# 톰캣 구현하기

## 3단계 리팩터링
1차 구조
- coyote -> catalina -> jwp 의존 관계

### 기능구현 목록
- [x] HttpRequest에 세션 추가 
- [x] HttpResponse 변경 및 수정
- [x] Servlet, DispatcherServlet 구현
- [x] HttpRequest와 세션 생명 주기 수정
- [x] RequestMapping 클래스 생성
- [x] 패키지 분리 및 핸들러 인터페이스와 컨트롤러 추상클래스 생성
- [x] 패키지 분리 및 컨트롤러 클래스 수정
