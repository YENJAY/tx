#!/bin/sh
# Example: mvn_run.sh 2015 11 20
<<<<<<< HEAD
mkdir -p output/bband output/char output/intermediate  output/results output/transaction input/
f=$1$2$3.log
echo Processing $f
mvn exec:java -Dexec.mainClass=oracle.HighTide -Dexec.args=input/$1$2/$f | grep -v INFO | tee output/results/$1$2$3.txt
=======
mkdir -p output/bband output/char output/intermediate  output/results output/transaction
f=input/Daily_$1_$2_$3_C.rpt
str=${f:6:16}
echo $str
echo Processing MTX...
grep -a MTX $f | tr "," " " | awk '{print $1" "$4" "$7}' | sort -n > output/intermediate/${str}_MTX.txt
mvn exec:java -Dexec.mainClass=oracle.Oracle -Dexec.args=output/intermediate/${str}_MTX.txt | grep -v INFO | tee output/results/${str}_MTX.txt

# echo Processing TX...
# grep -a ",TX" $f | tr "," " " | awk '{print $1" "$4" "$7}' | sort -n > output/intermediate/${str}_TX.txt
# mvn exec:java -Dexec.mainClass=oracle.Oracle -Dexec.args=output/intermediate/${str}_TX.txt | grep -v INFO | tee output/results/${str}_TX.txt
>>>>>>> e7bd6d7153a49abbde38725087452937464856b7
