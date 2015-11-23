# Example: run_all.bat 2015 11 20
source ParseRawData.sh $1 $2 $3
# source run_KBarBuilder.sh input/$1$2$3_MTX.txt > output/$1$2$3_KBar.txt
source run_bband.sh input/$1$2$3_MTX.txt | tee output/$1$2$3_BBand.txt
