#!/bin/sh
# ---------------------------------------------------------------------------
# 启动调试，简化命令输入，启动后，等待调试器连接
# ---------------------------------------------------------------------------
echoUsage() {
    # shellcheck disable=SC2086
    myname=$(basename $0)
    echo "
Usage: $myname [options]

    Options:
        -h   display help message
        -P   profile
        -i   run install before start debug
" >&2
}
# 变量初始化

pProfile="dev"
pExecInstall=false

while getopts ":hiP:" opt; do
    case $opt in
    "h")
        echoUsage
        exit 0
        ;;
    "i")
        pExecInstall=true
        ;;
    "P")
        pProfile=$OPTARG
        ;;
    ":")
        echo "No argument value for option $OPTARG"
        echoUsage
        exit 0
        ;;
    "?")
        echo "Unknown option $OPTARG"
        echoUsage
        exit 0
        ;;
    *)
        echo "Unknown error while processing options"
        echoUsage
        exit 1
        ;;
    esac
done

if [ $pExecInstall = true ]; then
    ./mvnw clean install -Dmaven.test.skip=true -U -amd
    sleep 3
fi
echo "starting debug with profile $pProfile"

./mvnw spring-boot:run -Dspring-boot.run.profiles="$pProfile" \
    -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
