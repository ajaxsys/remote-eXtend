@echo off

pushd %~dp0..\


pushd src\main\webapp\
call htm21 downloader.html
call htm21 entry.html

if NOT EXIST "allin1_downloader.html" (
	echo Please install `htm21` command
	goto end
)
popd


move src\main\webapp\allin1_downloader.html src\main\resources\
move src\main\webapp\allin1_entry.html src\main\resources\


echo Publish successfully

popd
:end
pause
