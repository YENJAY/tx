#!/bin/sh
# Example: mvn_run.sh 2015 11 20
mkdir -p output/bband output/char output/intermediate  output/results output/transaction input/
f=$1$2$3.log
echo Processing $f
mvn exec:java -Dexec.mainClass=oracle.Relativity -Dexec.args=input/$1$2/$f | grep -v INFO | tee output/results/$1$2$3.txt
