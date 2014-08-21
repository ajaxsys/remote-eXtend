pushd %~dp0..\

call mvn jetty:run -Djetty.port=9876
