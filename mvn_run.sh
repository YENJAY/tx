#!/bin/sh
source ./ParseRawData.sh $1 $2 $3
args=input/$1$2$3_MTX.txt
echo $args
mvn exec:java -Dexec.mainClass=oracle.bband.Oracle -Dexec.args=$args
