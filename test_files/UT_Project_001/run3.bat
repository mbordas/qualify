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
 
java -cp !CLASSPATH!;bin Harness qualify.TestHarness -option_file options_3.xml
