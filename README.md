# java-spring-jdbc

우아한형제들 김영한 님의 인프런 강의 '스프링 DB 1편 - 데이터 접근 핵심 원리'을 수강하면서 정리하기 위한 레포지토리

---

## 프로젝트 초기 설정

<details>
<summary>접기/펼치기 버튼</summary>
<div markdown="1">

### start.spring.io
![START_SPRING_IO.png](img/START_SPRING_IO.png)
아래의 설정 후 Generate를 클릭 > 압축파일 받아짐 > 풀기 > 인텔리제이에서 해당 폴더의 build.gradle 선택해서 프로젝트 빌드
- Project : Gradle Project
- Language: java
- Spring Boot : 2.6.6 (뒤에 괄호가 붙지 않은 가장 최신 버전을 사용)
- Project Metadata
  - Group
  - Artifact, Name
  - Package name : 자동적으로 `Group.Name`으로 작성됨
  - Packaging : Jar
  - Java : 11

### Dependencies
- Lombok
- JDBC API
- H2 Database : Driver Manager

### build.gradle 설정
```groovy
//테스트에서 lombok 사용
testCompileOnly 'org.projectlombok:lombok'
testAnnotationProcessor 'org.projectlombok:lombok'
```
- Dependencies에 추가 (테스트 코드에서 Logger 사용 목적)
- 인텔리제이 우측 상단의 gradle > Reload All Gradle Projects

### H2 데이터베이스 설치
![ExternalLibraries.png](img/ExternalLibraries.png)
![SPRING_BOOT_DEPENDENCY_H2.png](img/SPRING_BOOT_DEPENDENCY_H2.png)

- [h2 홈페이지](https://www.h2database.com/html/main.html) > All Downloads > Archive Downloads
- 스프링부트에서 지원하는 버전을 확인하고 적절한 버전을 사용
  - 2022.04.12 기준으로 스프링부트에서 지원하는 버전은 1.4.200 [[링크](https://docs.spring.io/spring-boot/docs/current/reference/html/dependency-versions.html#appendix.dependency-versions)]
    - 확인방법 : [Spring 공식 페이지](https://spring.io/) - Projects - [Spring Boot](https://spring.io/projects/spring-boot) - Learn - 현재 버전의 [Reference Doc](https://docs.spring.io/spring-boot/docs/current/reference/html/) > [Dependency Versions](https://docs.spring.io/spring-boot/docs/current/reference/html/dependency-versions.html#appendix.dependency-versions)

### H2 데이터베이스 실행
![RUN_H2_IN_TERMINAL.jpg](img/RUN_H2_IN_TERMINAL.jpg)
![JDBC_URL_BEFORE_MVDB_CREATED.png](img/JDBC_URL_BEFORE_MVDB_CREATED.png)
![mvdb_created.jpg](img/mvdb_created.jpg)
![JDBC_URL_AFTER_MVDB_CREATED.png](img/JDBC_URL_AFTER_MVDB_CREATED.png)
- 압축을 풀고, `h2>bin` 폴더에 들어간다.
- h2 실행 쉘 스크립트를 실행한다.
  - windows : `.\h2.bat`을 입력하여 실행
  - mac/linux : `chmod 755 h2.sh` 입력하여 권한 부여 후,  `.\h2.sh`을 입력하여 실행
- 주소창의 경로를 `localhost:8082/...` 로 변경하여 접속
- JDBC URL에 `jdbc:h2/~/java-spring-jdbc` 입력하여 연결하면 로컬드라이브에서 `~/java-spring-jdbc.mv.db` 생성됨
  - 여기서 지정한 url로 최초 접속하면 로컬드라이브의 해당 경로의 파일을 생성하고 접근
  - 이후부터는 파일에 직접 접근
- 이후에는 DB 접속시 `jdbc:h2:tcp://localhost/~/java-spring-jdbc`를 통해 접근함.
  - 이후부터는 이 URL로 접근함.

### 테이블 생성
```sql
DROP TABLE member if exists cascade;

CREATE TABLE member (
  member_id varchar(10),
  money integer not null default 0,
  primary key (member_id)
);

insert into member(member_id, money) values('hi1', 10000);
insert into member(member_id, money) values('hi2', 20000);
```
![AFTER_CREATE_TABLE.png](img/AFTER_CREATE_TABLE.png)
- drop table ...
  - DROP TABLE 테이블명 : 테이블 제거
  - if exists : 해당 테이블이 없어도 오류를 내지 않고, 알림 메시지만 보duwna
  - CASCADE : 해당 테이블과 의존성 관계가 있는 모든 객체들도 함께 삭제한다. 물론, 삭제될 다른 객제와 관계된 또 다른 객체들도 함께 삭제
- 테이블 생성 DDL
- 원활한 테스트를 위해 사용자 2명 초기 추가

</div>
</details>

---

## JDBC의 이해

<details>
<summary>접기/펼치기 버튼</summary>
<div markdown="1">

### JDBC(Java Database Connectivity)

![JDBC.png](img/JDBC.png)

DBMS 종류에 관계 없이, Java에서 표준화된 사용방법으로 DBMS에 접속할 수 있도록 하는 Java API

### 기존?
- 어플리케이션 서버 측에서, 수동으로 DB측에 커넥션을 연결하고, SQL을 전달하고, 응답결과를 받아오는 로직을 전부 처리해야했음.
- DBMS마다 이 사용방식이 달랐음.

### JDBC 표준 인터페이스
java는 개발자들이 편리하게 데이터베이스에 접근할 수 있도록 표준 인터페이스를 정의함.
각각의 DB 벤더측에서 제공하는 Jdbc Driver는 다음 인터페이스들을 구현한다.
- `java.sql.Connection` : DBMS 연결
- `java.sql.Statement` : SQL을 담은 내용
- `java.sql.ResultSet` : SQL 요청 응답

### JDBC Driver (벤더별 JDBC 표준 API 구현체)
- JDBC 표준 인터페이스를 각각의 DB 벤더측에서 구현해서 라이브러리를 통해 제공함
- 애플리케이션 서버 측에서, 필요한 JDBC Driver를 라이브러리로 등록해둠

### DriveManager (JDBC Drive 관리)
라이브러리에 등록된 여러 DB 드라이버들을 관리하고, 커넥션을 요청하여 획득하는 기능을 제공
- 라이브러리에 등록된 JDBC 드라이버 목록을 자동으로 인식
- URL, 사용자명, 비밀번호 등 접속에 필요한 추가적인 정보
- URL 정보를 확인하여 DriveManager가 처리할 수 있는 요청인지 확인
  - 처리할 수 없으면 다음 드라이버로 순서가 넘어감.

### 동작 원리
- 어플리케이션 로직에서 DriveManager측에 `getConnection()`을 호출하여 커넥션을 요청함.
- 등록된 라이브러리 중 조건에 맞는 드라이버를 찾아 커넥션을 요청, DBMS에 커넥션을 연결
- SQL을 Statement에 담고, 전달
- ResultSet에 응답 데이터를 가져옴


### 의의
- DBMS측과의 커넥션 생성, 데이터 질의/응답 로직을 JDBC 인터페이스를 사용하여 편리하게 하기 위함
- 사용자는 JDBC 표준 인터페이스를 사용할 줄 알기만 하면 된다. 물론 각각의 SQL은 DB마다 사용법이 다른 부분이 있음.
  - ANSI SQL이라는 표준이 있지만, 일반적인 부분만 공통화 했기 때문에 한계가 존재

</div>
</details>

---

## JDBC와 최신 데이터 접근 기술

<details>
<summary>접기/펼치기 버튼</summary>
<div markdown="1">

1. JDBC 직접 사용
   - 애플리케이션 로직 - JDBC 인터페이스를 통해 SQL을 DB에 전달
   - 
2. SQL Mapper 사용
   - 애플리케이션 로직 - SQL을 SQL Mapper에 전달(다양한 편의 제공) - JDBC를 통해 SQL을 DB에 전달
   - 장점 : JDBC의 반복 코드 제거, SQL 응답 결과를 객체로 편리하게 변환
   - 단점 : 개발자가 SQL을 직접 작성해야함...

3. ORM 기술
   - 애플리케이션 로직 - 객체를 JPA에 전달(JPA 구현체) - JDBC를 통해 SQL을 DB에 전달
   - 객체를 RDBMS의 테이블과 매핑
   - 단점 : 사전에 학습해야할 것이 매우 많음. (OOP, RDBMS)

### 결론
- 어떤 기술을 택하든 내부적으로 JDBC를 사용
- JDBC를 직접 사용하지 않더라도 내부적으로 기본원리를 알아두어야 한다.

</div>
</details>

---

## 데이터베이스 연결

<details>
<summary>접기/펼치기 버튼</summary>
<div markdown="1">

### 커넥션 획득(DriverManager)
```java
Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
```
- 등록된 라이브러리 중 조건에 맞는 드라이버를 찾아 커넥션을 요청, DBMS에 커넥션을 연결

### 커넥션(Connection)
- JDBC 표준 인터페이스 : `java.sql.Connection`
- 각 벤더별로 구현
  - 예) H2 데이터베이스 : org.h2.jdbc.JdbcConnection

</div>
</details>

---

## JDBC - insert, update, delete

<details>
<summary>접기/펼치기 버튼</summary>
<div markdown="1">

```java

    public Member save(Member member) throws SQLException {
        String sql = "INSERT INTO MEMBER (member_id, money)\n" +
                "values (?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            log.error("db error : {}", e);
            throw e;
        } finally {
            close(conn, pstmt, null);
        }
    }
```

### SQL
- 데이터베이스에 전달할 SQL
  - Preparestatement로 넘길 때, 넘길 인자들은 `?`로 표현

### PrepareStatement
Statement의 하위 클래스. `?`를 통한 바인딩을 가능하게 한다. SQL Injection을 방지하기 위해서는 PrepareStatement를 써야함.
- `con.prepareStatement(sql)` : DB에 전달할 SQL 및 파라미터로 전달할 데이터들을 준비
  - `sql`
  - `pstmt.setString(1, member.getMemberId())` : 첫번째 `?`에 값 지정  
  - `pstmt.setInt(1, member.getMoney())` : 두번째 `?`에 값 지정.
- `pstmt.executeUpdate()` : statement를 통해 준비된 SQL을 커넥션을 통해 실제 DB에 전달하여 데이터를 조작
  - DB의 데이터를 조작하는 INSERT, DELETE, UPDATE문은 `executeUpdate()`를 호출
  - executeUpdate는 실제 반영된 DB row 수를 반환

### 리소스 정리
- ResultSet 반환 -> Statement 반환 -> Connection 반환

</div>
</details>

---

## JDBC - select

<details>
<summary>접기/펼치기 버튼</summary>
<div markdown="1">

```java
    public Member findById(String memberId) throws SQLException {
        String sql = "SELECT member_id, money\n" +
                "FROM member\n" +
                "WHERE member_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return Member.builder()
                        .memberId(rs.getString("member_id"))
                        .money(rs.getInt("money"))
                        .build();
            } else {
                throw new NoSuchElementException(
                        String.format("Member Not Found : memberId= %s", memberId)
                );
            }
        } catch (SQLException e) {
            log.error("db error : {}", e);
            throw e;
        } finally {
            close(conn, pstmt, rs);
        }
    }
```
- DB에서 데이터를 조회하는 SELECT문의 결과는 `pstmt.executeQuerey()` 메서드를 통해 얻어온다.
  - 이 메서드의 반환 타입은 ResultSet이다.

### ResultSet 
![Result_Cursor.jpg](img/ResultSet_Cursor.jpg)
- `rs.next()` : ResultSet의 커서를 다음으로 이동시키고 결과를 가져온 뒤, 결과가 존재하면 true, false를 반환
    - ResultSet의 커서는 최초에 어떤 데이터도 가리키고 있지 않아서, 첫번째 결과부터 받아오려면 `rs.next()`로 커서를 옮겨야한다.
- 결과가 단건이면 if문으로 감싸 처리하고, 여러건이면 while문으로 처리한다.

</div>
</details>

---

