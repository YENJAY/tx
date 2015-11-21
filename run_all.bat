@REM Example: run_all.bat 20151120
call run_KBarBuilder.bat input\%1_MTX.txt > output\%1_KBar.txt
run_bband.bat output\%1_KBar.txt > output\%1_BBand.txt
