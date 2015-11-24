#!/bin/sh
# Example: mvn_run.sh 2015 11 20
for f in input/*
do
    str=${f:6:16}
    echo $str
    echo Processing MTX...
    grep -a MTX $f | tr "," " " | awk '{print $1" "$4" "$7}' | sort -n > output/${str}_MTX.txt
    mvn exec:java -Dexec.mainClass=oracle.bband.Oracle -Dexec.args=output/${str}_MTX.txt | tee output/${str}.txt
    # echo Processing TX...
	# grep -a ",TX" $f | tr "," " " | awk '{print $1" "$4" "$7}' | sort -n > output/${str}_TX.txt
    # mvn exec:java -Dexec.mainClass=oracle.bband.Oracle -Dexec.args=output/${str}_TX.txt | tee output/results/${str}.txt
done
