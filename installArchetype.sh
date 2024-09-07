#!/bin/bash
currentDir=$(pwd)
printf "\033[0;32m准备环境 ...\r\n\033[0m"
rm -rf target > /dev/null
mv .gitignore __gitignore__ > /dev/null
printf "\033[0;32m创建并安装原型 ...\r\n\033[0m"
sleep 3 > /dev/null
./mvnw archetype:create-from-project -Darchetype.properties=./archetype.properties
cd target/generated-sources/archetype || exit 1
../../../mvnw install > /dev/null
printf "\033[0;32m安装完毕\033[0m"
printf "\033[0;32m可以使用 mvn archetype:generate 命令使用原型创建新的工程，具体参见 READEME.md\r\n\033[0m"
sleep 3 > /dev/null
cd "$currentDir" || exit 1
mv __gitignore__ .gitignore > /dev/null
exit 0