#!/bin/sh
# Example: mvn_run.sh 2015 11 20
mkdir -p output/bband output/char output/intermediate  output/results output/transaction
f=input/Daily_$1_$2_$3_C.rpt
str=${f:6:16}
echo $str
echo Processing MTX...
grep -a MTX $f | tr "," " " | awk '{print $1" "$4" "$7}' | sort -n > output/intermediate/${str}_MTX.txt
mvn exec:java -Dexec.mainClass=oracle.bband.Oracle -Dexec.args=output/intermediate/${str}_MTX.txt | grep -v INFO | tee output/results/${str}_MTX.txt

# echo Processing TX...
# grep -a ",TX" $f | tr "," " " | awk '{print $1" "$4" "$7}' | sort -n > output/intermediate/${str}_TX.txt
# mvn exec:java -Dexec.mainClass=oracle.bband.Oracle -Dexec.args=output/intermediate/${str}_TX.txt | grep -v INFO | tee output/results/${str}_TX.txt
