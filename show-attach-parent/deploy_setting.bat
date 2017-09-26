@echo off
mvn --settings C:\Users\Administrator\.m2\settings_jdk8_2.xml clean install deploy -Dmaven.test.skip=true
pause