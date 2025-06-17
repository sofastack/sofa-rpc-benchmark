#!/usr/bin/env bash

usage() {
    echo "Usage: ${PROGRAM_NAME} command dirname"
    echo "command: [m|s|p|f|t]"
    echo "         -m [profiling|benchmark], specify benchmark mode"
    echo "         -s hostname, host name"
    echo "         -p port, port number"
    echo "         -f output file path"
    echo "         -t client thread nums"
    echo "         -e other system property"
    echo "         -a other args"
    echo "dirname: test module name"
}

build() {
    mvn --projects benchmark-base,client-base,server-base,${PROJECT_DIR} clean package
}

java_options() {
    JAVA_OPTIONS="-server -Xmx1g -Xms1g -XX:MaxDirectMemorySize=1g -XX:+UseG1GC -Djmh.ignoreLock=true"
    if [ "x${MODE}" = "xprofiling" ]; then
        JAVA_OPTIONS="${JAVA_OPTIONS} \
            -XX:+UnlockCommercialFeatures \
            -XX:+FlightRecorder \
            -XX:StartFlightRecording=duration=30s,filename=${PROJECT_DIR}.jfr \
            -XX:FlightRecorderOptions=stackdepth=256"
    fi
}

run() {
    if [ -d "${PROJECT_DIR}/target" ]; then
        JAR=`find ${PROJECT_DIR}/target/*.jar | head -n 1`
        echo
        echo "RUN ${PROJECT_DIR} IN ${MODE:-benchmark} MODE"
        CMD="java ${JAVA_OPTIONS} -Dserver.host=${SERVER} -Dserver.port=${PORT} -Dbenchmark.output=${OUTPUT} -Dthread.num=${THREADNUM} ${SYSTEM_PROPS} -jar ${JAR} ${OTHERARGS}"
        echo "command is: ${CMD}"
        echo
        ${CMD}
    fi
}

PROGRAM_NAME=$0
MODE="benchmark"
SERVER="localhost"
PORT="12200"
OUTPUT=""
OPTIND=1
OTHERARGS=""
THREADNUM=""
SYSTEM_PROPS=""

while getopts "m:s:p:f:t:e:a:" opt; do
    case "$opt" in
        m)
            MODE=${OPTARG}
            ;;
        s)
            SERVER=${OPTARG}
            ;;
        p)
            PORT=${OPTARG}
            ;;
        f)
            OUTPUT=${OPTARG}
            ;;
        t)
            THREADNUM=${OPTARG}
            ;;
        e)
            SYSTEM_PROPS=${OPTARG}
            ;;
        a)
            OTHERARGS=${OPTARG}
            ;;
        ?)
            usage
            exit 0
            ;;
    esac
done

shift $((OPTIND-1))
PROJECT_DIR=$1

if [ ! -d "${PROJECT_DIR}" ]; then
    usage
    exit 0
fi

build
java_options
run






