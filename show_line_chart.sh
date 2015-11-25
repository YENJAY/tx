#!/bin/sh
# Example: show_line_chart.sh 2015 11 20
str=Daily_$1_$2_$3_C.rpt
echo $str
mvn exec:java -Dexec.mainClass=oracle.chart.LineChart -Dexec.args=output/results/${str}_MTX.txt
