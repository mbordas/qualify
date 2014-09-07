@echo off
 setLocal EnableDelayedExpansion
 set CLASSPATH="
 for /R ../../target/dependency %%a in (*.jar) do (
   set CLASSPATH=!CLASSPATH!;%%a
 )
 for /R ../../target %%a in (*.jar) do (
   set CLASSPATH=!CLASSPATH!;%%a
 )
 set CLASSPATH=!CLASSPATH!"
 echo !CLASSPATH!
 
javac src/Harness.java -sourcepath src -d bin -cp !CLASSPATH! 
