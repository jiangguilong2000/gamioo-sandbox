set "TOOLPATH=%~dp0flatc.exe"
cd /d %~dp0
:: %TOOLPATH% --java -o ../java ../java/io/gamioo/sandbox/fbs/SkillFire.fbs
%TOOLPATH% --binary -o ../resources ../java/io/gamioo/sandbox/fbs/SkillFire.fbs message.json