#!/bin/sh
# Example: mvn_run.sh 2015 11 20
grep -a MTX src/main/resources/input/Daily_$1_$2_$3_C.rpt | tr "," " " | awk '{print $1" "$4" "$7}' | sort -n > src/main/resources/input/$1$2$3_MTX.txt
mvn exec:java -Dexec.mainClass=oracle.bband.Oracle -Dexec.args=input/$1$2$3_MTX.txt
