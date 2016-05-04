READ ME FIRST:
Some steps need to done on the system, on which this test needs to be run.
Those are as below.
Pre Configuration:
•	Maven should be installed on the system, preferably maven 3
•	This project is coded and test with JDK 1.8. It don’t uses any method specific  to JDK 1.8, But It will be better, if we have JDK 1.8 installed and configured on the system
•	We need to have the files test.txt and test.JPG at the appropriate location and that location needs to be configured in \\GoogleDrive\src\test\resources\config.properties
•	If we need to test this test using selenium HUB, that needs to be configured as appropriate
•	The browser on which we need to run the test, should be installed on the system, where we want to run this test
•	We need to have files like chromedriver.exe, IEDriverServer_32bit.exe, IEDriverServer_64bit.exe inside the folder ..// GoogleDrive/lib/.

Changes that need to be made before run:
•	Update  \\GoogleDrive\src\test\resources\config.properties for keys, But don’t change the values for keys marked read-only
•	Update  \\GoogleDrive\testng.xml , refer comments in the file
•	If we are changing the upload files, than they should be of similar size as the one provided, else increase/decrease in upload time will need to be handled in test case, by adjusting the default wait or time out
Steps to Execute
•	Unzip the GoogleDrive.zip to the desired location
•	Go to command prompt and then go to the …//GoogleDrive/. Folder
•	Command “mvn clean test” will clean, complie and then run the tests
Logs and reports	
•	For logs of execution refer the console and logs are saved to …//GoogleDrive/. Folder by the name test.log and old logs will automatically get renamed by appending the current time of execution on latest run.
•	For html report refer  ..//GoogleDrive/target/surefire-reports/emailable-report.html

Please see
I have run the test on my local with-out grid option, as I don’t have the required infrastructure.
But the test code supports selenium grid execution. The clean run logs and report is attached along.
