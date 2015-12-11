#!/bin/sh
# Example: mvn_run_month.sh 2015 11
mkdir -p output/bband output/char output/intermediate  output/results output/transaction input/
for f in input/$1$2/*.log
do
    echo Processing $f
    mkdir -p output/results/$1$2/
    # input/201509/201509.log
    A=${f:13:4}
    B=${f:17:2}
    C=${f:19:2}
    bash mvn_run_single.sh $A $B $C
done
