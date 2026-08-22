# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

Spring Boot 4.1.1 기반 Java 애플리케이션. Spring Initializr로 생성된 초기 상태이며, 현재 코드는 부트스트랩 클래스(`CoreApplication`)와 컨텍스트 로딩 테스트(`CoreApplicationTests`)뿐이다.

- Gradle 그룹/아티팩트: `hello` / `core`, 버전 `0.0.1-SNAPSHOT`
- 베이스 패키지: `hello.core`
- 빌드 도구: Gradle 9.5.1 (wrapper 사용)

## 명령어

빌드·테스트는 항상 wrapper(`./gradlew`)로 실행한다. 저장소에 Gradle이 별도로 설치돼 있다고 가정하지 않는다.

```bash
./gradlew build              # 컴파일 + 테스트 + 패키징
./gradlew bootRun            # 애플리케이션 실행
./gradlew test               # 전체 테스트
./gradlew clean              # 빌드 산출물 삭제
```

단일 테스트 실행:

```bash
./gradlew test --tests 'hello.core.CoreApplicationTests'              # 클래스 단위
./gradlew test --tests 'hello.core.CoreApplicationTests.contextLoads' # 메서드 단위
./gradlew test --tests '*CoreApplication*'                            # 패턴 매칭
```

테스트 리포트는 `build/reports/tests/test/index.html`에 생성된다.

## 아키텍처·설정 제약

- **Java 25 toolchain** — `build.gradle`의 `java.toolchain.languageVersion`이 25로 고정돼 있다. Gradle이 JDK 25를 찾지 못하면 빌드가 실패하므로, 로컬 JDK 버전을 임의로 낮추지 말고 toolchain 설정을 유지한다. IntelliJ 쪽 설정(`.idea/compiler.xml`, `.idea/misc.xml`)도 bytecode target 25 / JDK_25로 맞춰져 있다.
- **웹 스타터가 없다** — 의존성은 `spring-boot-starter`(core)만 포함한다. 따라서 `bootRun`은 서블릿 컨테이너를 띄우지 않고 컨텍스트 초기화 후 곧바로 종료된다. HTTP 엔드포인트가 필요하면 먼저 `spring-boot-starter-web` 의존성을 추가해야 한다.
- **테스트 플랫폼은 JUnit 5(Jupiter)** — `tasks.named('test') { useJUnitPlatform() }`로 설정돼 있고, `junit-platform-launcher`가 `testRuntimeOnly`로 들어가 있다. JUnit 4 API(`org.junit.Test`)는 사용할 수 없다.
- **컴포넌트 스캔 범위** — `@SpringBootApplication`이 붙은 `CoreApplication`이 `hello.core` 패키지에 있으므로, 스캔 대상은 `hello.core` 및 그 하위 패키지다. 새 빈/설정 클래스는 이 하위에 둔다.
- **애플리케이션 설정** — `src/main/resources/application.properties`에 `spring.application.name=core`만 있다. 설정 추가 시 이 파일을 사용한다(YAML 파일은 존재하지 않는다).

## 코드 스타일

기존 소스는 **탭 인덴트**를 사용한다(Spring Initializr 기본값). `.idea/google-java-format.xml`에서 google-java-format은 **비활성화**돼 있으므로 자동 포매터를 켜서 전체 파일을 재정렬하지 않는다.

## 줄바꿈(EOL) 규칙

`.gitattributes`에 강제돼 있다: `gradlew`는 LF, `*.bat`은 CRLF, `*.jar`는 binary. 이 파일들을 편집할 때 줄바꿈을 바꾸지 않는다.
