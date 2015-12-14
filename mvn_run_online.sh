#!/bin/sh
<<<<<<< HEAD
mkdir -p output/bband output/char output/intermediate  output/results output/transaction input/
mvn exec:java -Dexec.mainClass=oracle.HighTide 
=======
# Example: mvn_run.sh 2015 11 20
mvn exec:java -Dexec.mainClass=oracle.Oracle
>>>>>>> e7bd6d7153a49abbde38725087452937464856b7
