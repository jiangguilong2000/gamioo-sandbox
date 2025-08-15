set "TOOLPATH=%~dp0protoc.exe"
cd /d %~dp0
%TOOLPATH% --java_out=../java --proto_path=../java/io/gamioo/sandbox/proto ../java/io/gamioo/sandbox/proto/skill.proto


