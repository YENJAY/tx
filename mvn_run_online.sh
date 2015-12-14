#!/bin/sh
mkdir -p output/bband output/char output/intermediate  output/results output/transaction input/
mvn exec:java -Dexec.mainClass=oracle.HighTide
