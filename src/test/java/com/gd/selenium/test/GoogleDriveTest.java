package com.gd.selenium.test;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.gd.selenium.utility.Log;

public class GoogleDriveTest {
	private RemoteWebDriver driver; 
	private Properties configProp;
	private Properties locators;
	private String testCaseName;
	private String os;
	@BeforeTest
	@Parameters({ "osName", "browser","grid"})
	public void testSetUp(String osName, String browser, String grid) {
		testCaseName = new Object(){}.getClass().getEnclosingMethod().getName();
		Log.startTestCase(testCaseName);
		configProp = getProperties("/config.properties");
		locators = getProperties("/locators.properties");
		Platform platform = null;
		if(osName.equalsIgnoreCase("windows")){
			platform = Platform.WINDOWS;
			os="win";
		}
		else if(osName.equalsIgnoreCase("Linux")){
			platform = Platform.LINUX;
			os="unix";
		}
		else if(osName.equalsIgnoreCase("MAC")){
			platform = Platform.MAC;
			os="unix";
		}
		Log.info("Launching the Browser...." + browser);
		if(browser.equalsIgnoreCase("firefox")){
			if(grid.equalsIgnoreCase("off")){
				driver = new FirefoxDriver();				
			}
			else{
				DesiredCapabilities capabilities = new DesiredCapabilities();
				capabilities.setPlatform(platform);
				capabilities.setBrowserName("firefox");
				try {
					driver = new RemoteWebDriver(new URL(configProp.getProperty("hub.url")),capabilities);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
			Log.info("Browser Launched successfully....");
		}
		else if(browser.equalsIgnoreCase("chrome")){
			if(grid.equalsIgnoreCase("off")){
				System.setProperty("webdriver.chrome.driver","lib\\chromedriver.exe");
				driver = new ChromeDriver();
			}
			else{
				DesiredCapabilities capabilities = new DesiredCapabilities();
				capabilities.setPlatform(platform);
				capabilities.setBrowserName("chrome");
				try {
					driver = new RemoteWebDriver(new URL(configProp.getProperty("hub.url")),capabilities);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
			Log.info("Browser Launched successfully....");
		}
		else if(browser.equalsIgnoreCase("ie32bit")){
			if(grid.equalsIgnoreCase("off")){
				System.setProperty("webdriver.ie.driver","lib\\IEDriverServer_32bit.exe");
				DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
				ieCapabilities.setCapability(
						InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
						true);
				driver = new InternetExplorerDriver(ieCapabilities);
			}
			else{
				DesiredCapabilities capabilities = new DesiredCapabilities();
				capabilities.setPlatform(platform);
				capabilities.setBrowserName("internet explorer");
				try {
					driver = new RemoteWebDriver(new URL(configProp.getProperty("hub.url")),capabilities);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
			Log.info("Browser Launched successfully....");
		}
		else{
			Log.error(browser + " Browser is not yet Supported...");
			Assert.fail();
		}
	}

	@Test
	public void testInvalidLogin(){
		testCaseName = new Object(){}.getClass().getEnclosingMethod().getName();
		Log.startTestCase(testCaseName);
		String waut = configProp.getProperty("waut");
		driver.get(waut);
		WebElement email = findElementSafe("login.id.email");
		WebElement next = findElementSafe("login.id.next");
		email.sendKeys(configProp.getProperty("login.emailId"));
		Log.info("Entering the email Id");
		next.submit();
		WebElement passwrd = findElementSafe("login.id.passwrd");
		WebElement signIn = findElementSafe("login.id.signin");
		passwrd.sendKeys(configProp.getProperty("login.wrong.passwrd"));
		Log.info("Entering the wrong Password");
		signIn.submit();
		Log.info("Validating the invalid log in message");
		WebElement errorMessageElement = findElementSafe("login.id.invalidLoginMsg");
		String actual = errorMessageElement.getText();
		String expected = configProp.getProperty("login.invalid.login.msg");
		Assert.assertEquals(actual, expected, "Error message is not matching");
	}

	@Test(dependsOnMethods="testInvalidLogin")
	public void testValidLogin(){
		testCaseName = new Object(){}.getClass().getEnclosingMethod().getName();
		Log.startTestCase(testCaseName);
		WebElement passwrd = findElementSafe("login.id.passwrd");
		WebElement signIn = findElementSafe("login.id.signin");
		passwrd.sendKeys(configProp.getProperty("login.correct.passwrd"));
		Log.info("Entering the correct password");
		signIn.submit();

		WebElement userNameElement = findElementSafe("main.xpath.loggedin.user");
		if(userNameElement==null){
			Log.info("Google security comes into picture. Need to provide answer to security Question");
			WebElement securityAnswer = findElementSafe("login.id.security.answer");
			if(securityAnswer!=null){
				securityAnswer.sendKeys(configProp.getProperty("login.security.answer"));
				WebElement submitBttn = findElementSafe("login.id.security.answer.submit");
				submitBttn.submit();
				userNameElement = findElementSafe("main.xpath.loggedin.user");
				Assert.assertNotNull(userNameElement, "Log in has some problem");
			}
		}
		Log.info("Validating user name for confirming successfull login");
		String actual = userNameElement.getText();
		String expected = configProp.getProperty("main.user.name");
		Assert.assertEquals(actual, expected, "Login is not successful");
	}

	@Test(dependsOnMethods="testValidLogin")
	public void testFileUpload(){
		testCaseName = new Object(){}.getClass().getEnclosingMethod().getName();
		Log.startTestCase(testCaseName);
		Log.info("Refereshing the webPage");
		driver.navigate().refresh();
		String filePath = configProp.getProperty("main.upload.file.path");
		Log.info("Getting number of files before start of new file upload, if any");
		List<WebElement> documentsElement = findElementsSafe("main.xpath.uploaded.file","file");
		int initialNumOfDocs = documentsElement.size();
		Log.info("old files are : "+ initialNumOfDocs);
		Log.info("clicking on my drive for upload options");
		WebElement myDriveElement = findElementSafe("main.xpath.mydrive.options");
		myDriveElement.click();
		Log.info("Clicking on upload file option");
		WebElement  uploadFileElement = findElementSafe("main.xpath.upload.file.link");
		Actions actions = new Actions(driver);
		actions.moveToElement(uploadFileElement);
		actions.sendKeys(Keys.ENTER);
		actions.perform();
		Log.info("selecting file using java Robot Awt");
		try{
			StringSelection ss = new StringSelection(filePath);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
			Robot rb =new Robot();
			rb.keyPress(KeyEvent.VK_CONTROL);
			sleep(2);
			rb.keyPress(KeyEvent.VK_V);
			sleep(2);
			rb.keyRelease(KeyEvent.VK_V);
			sleep(2);
			rb.keyRelease(KeyEvent.VK_CONTROL);
			sleep(2);
			rb.keyPress(KeyEvent.VK_ENTER);
			sleep(2);
			rb.keyRelease(KeyEvent.VK_ENTER);
		}
		catch(Exception ex){
			ex.printStackTrace();
			Log.error(ex.getMessage());
			Assert.fail("Robot code did not worked as supposed...");
		}
		sleep(2);
		Log.info("validating upload success message");
		WebElement uploadCompleteElement = findElementSafe("main.xpath.upload.complete.msg.link");
		Assert.assertNotNull(uploadCompleteElement, "Upload Message is not shown");
		Log.info("Refereshing the web page");
		driver.navigate().refresh();
		Log.info("getting new number of files");
		documentsElement = findElementsSafe("main.xpath.uploaded.file", "file");
		int finalNumOfDocs = documentsElement.size();
		Log.info("New number of files is  : " + finalNumOfDocs);
		Assert.assertEquals(finalNumOfDocs, initialNumOfDocs+1, "File Upload sync is not yet successfull");
	}

	@Test(dependsOnMethods="testFileUpload")
	public void testRenameFile(){
		testCaseName = new Object(){}.getClass().getEnclosingMethod().getName();
		Log.startTestCase(testCaseName);
		Log.info("Refereshing the webPage");
		driver.navigate().refresh();
		String newFileName = null;
		Log.info("Generating the unique name for file to be renamed");
		boolean uniqueFileNameFound = false;
		while(!uniqueFileNameFound){
			newFileName = System.currentTimeMillis() + ".txt";
			List<WebElement> filesWithNewName = findElementsSafe(By.xpath("//span[text()='"+ newFileName +"']"));
			if(filesWithNewName.size()==0){
				uniqueFileNameFound = true;
			}
		}
		Log.info("Unique name for renaming the file is : " + newFileName);
		Log.info("selecting and right clicking the file to be renamed");
		List<WebElement> documentsElement = findElementsSafe("main.xpath.uploaded.file");
		Actions actions = new Actions(driver);
		actions.moveToElement(documentsElement.get(0));
		actions.contextClick(documentsElement.get(0));
		actions.perform();
		sleep(2);
		Log.info("selecting the rename link");
		WebElement renameElement = findElementSafe("main.xpath.rename.link");		
		actions = new Actions(driver);
		actions.moveToElement(renameElement);
		actions.sendKeys(Keys.ENTER);
		actions.perform();
		Log.info("Clearing, typing and sending the new file name");
		WebElement renamePopUpElement = findElementSafe("main.xpath.rename.alert.input");
		if(renamePopUpElement==null){ 
			//this code is failing in IE so this code for trying again
			Log.info("Refereshing the webPage");
			driver.navigate().refresh();
			Log.info("selecting and right clicking the file to be renamed");
			documentsElement = findElementsSafe("main.xpath.uploaded.file");
			actions = new Actions(driver);
			actions.moveToElement(documentsElement.get(0));
			actions.contextClick(documentsElement.get(0));
			actions.perform();
			sleep(2);
			Log.info("selecting the rename link");
			renameElement = findElementSafe("main.xpath.rename.link");		
			actions = new Actions(driver);
			actions.moveToElement(renameElement);
			actions.sendKeys(Keys.ENTER);
			actions.perform();
		}
		sleep(2);
		String renameElementId = renamePopUpElement.getAttribute("for");
		String xPathForRenameInput = "//input[@id='" +  renameElementId +"']";
		WebElement renameInputElement = findElementSafe(By.xpath(xPathForRenameInput));
		Log.info("Clearing the input field");
		renameInputElement.clear();
		Log.info("Typing the new file name");
		renameInputElement.sendKeys(newFileName);
		WebElement okButtonElement = findElementSafe("main.xpath.rename.alert.ok");
		Log.info("Saving the new file name");
		okButtonElement.click();
		String filePath = configProp.getProperty("main.upload.file.path");
		String oldFileName = filePath.substring(filePath.indexOf('\\'));
		String xPAthForRenameEventElement = "//div[text()='" + oldFileName + " renamed to " + newFileName + "']";
		findElementSafe(By.xpath(xPAthForRenameEventElement));
		Log.info("Refreshing the web page");
		driver.navigate().refresh();
		Log.info("Confirming the sync of renamed file on drive");
		String xPathRenamedFile = "//span[text()='"+ newFileName +"']";
		WebElement newFileElement = findElementSafe(By.xpath(xPathRenamedFile));
		Assert.assertNotNull(newFileElement,"File Rename Is not Successfull");
	}

	@Test(dependsOnMethods="testValidLogin")
	public void testImageUpload(){
		testCaseName = new Object(){}.getClass().getEnclosingMethod().getName();
		Log.startTestCase(testCaseName);
		Log.info("Refereshing the webPage");
		driver.navigate().refresh();
		String imagePath = configProp.getProperty("main.upload.image.path");
		Log.info("Getting number of files before start of new file upload, if any");
		List<WebElement> documentsElement = findElementsSafe("main.xpath.uploaded.image","image");
		int initialNumOfDocs = documentsElement.size();
		Log.info("old files are : "+ initialNumOfDocs);
		Log.info("clicking on my drive for upload options");
		WebElement myDriveElement = findElementSafe("main.xpath.mydrive.options");
		myDriveElement.click();
		Log.info("Clicking on upload file option");
		WebElement  uploadFileElement = findElementSafe("main.xpath.upload.file.link");		
		Actions actions = new Actions(driver);
		actions.moveToElement(uploadFileElement);
		actions.sendKeys(Keys.ENTER);
		actions.perform();
		Log.info("selecting file using java Robot Awt");
		try{
			StringSelection ss = new StringSelection(imagePath);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
			Robot rb =new Robot();
			rb.keyPress(KeyEvent.VK_CONTROL);
			sleep(2);
			rb.keyPress(KeyEvent.VK_V);
			sleep(2);
			rb.keyRelease(KeyEvent.VK_V);
			sleep(2);
			rb.keyRelease(KeyEvent.VK_CONTROL);
			sleep(2);
			rb.keyPress(KeyEvent.VK_ENTER);
			sleep(2);
			rb.keyRelease(KeyEvent.VK_ENTER);
		}
		catch(Exception ex){
			ex.printStackTrace();
			Log.error(ex.getMessage());
			Assert.fail("Robot code did not worked as supposed...");
		}
		sleep(5);
		Log.info("validating upload success message");
		WebElement uploadCompleteElement = findElementSafe("main.xpath.upload.complete.msg.link");
		Assert.assertNotNull(uploadCompleteElement, "Upload Message is not shown");
		Log.info("Refereshing the web page");
		driver.navigate().refresh();
		Log.info("getting new number of files");
		documentsElement = findElementsSafe("main.xpath.uploaded.image","image");
		int finalNumOfDocs = documentsElement.size();
		Log.info("New number of files is  : " + finalNumOfDocs);
		Assert.assertEquals(finalNumOfDocs, initialNumOfDocs+1, "File Upload sync is not yet successfull");
	}

	@Test(dependsOnMethods="testRenameFile")
	public void testNoNetwork(){
		testCaseName = new Object(){}.getClass().getEnclosingMethod().getName();
		Log.startTestCase(testCaseName);
		Log.info("Refereshing the webPage");
		driver.navigate().refresh();
		String filePath = configProp.getProperty("main.upload.file.path");
		Log.info("clicking on my drive for upload options");
		WebElement myDriveElement = findElementSafe("main.xpath.mydrive.options");
		myDriveElement.click();
		Log.info("Clicking on upload file option");
		WebElement  uploadFileElement = findElementSafe("main.xpath.upload.file.link");		
		Actions actions = new Actions(driver);
		actions.moveToElement(uploadFileElement);
		actions.sendKeys(Keys.ENTER);
		actions.perform();
		Log.info("Disabling network connection");
		try {
			Runtime.getRuntime().exec(configProp.getProperty(os+".command.disable.network"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.info("selecting file using java Robot Awt");
		try{
			StringSelection ss = new StringSelection(filePath);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
			Robot rb =new Robot();
			rb.keyPress(KeyEvent.VK_CONTROL);
			sleep(2);
			rb.keyPress(KeyEvent.VK_V);
			sleep(2);
			rb.keyRelease(KeyEvent.VK_V);
			sleep(2);
			rb.keyRelease(KeyEvent.VK_CONTROL);
			sleep(2);
			rb.keyPress(KeyEvent.VK_ENTER);
			sleep(2);
			rb.keyRelease(KeyEvent.VK_ENTER);
		}
		catch(Exception ex){
			ex.printStackTrace();
			Log.error(ex.getMessage());
			try {
				Log.info("Enabling network connection");
				Runtime.getRuntime().exec(configProp.getProperty(os+".command.enable.network"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			Assert.fail("Robot code did not worked as supposed...");
		}
		sleep(8);
		if(driver instanceof FirefoxDriver) {
			try {
				Log.info("Enabling network connection if this is firefox browser");
				Runtime.getRuntime().exec(configProp.getProperty(os+".command.enable.network"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Log.info("Validating the offline message for no network connectivity");
		WebElement offlineMessageElement = findElementSafe("main.xpath.gDrive.offline.alert");
		Assert.assertNotNull(offlineMessageElement, "Offline Message is not shown");
		try {
			Log.info("Enabling network connection");
			Runtime.getRuntime().exec(configProp.getProperty(os+".command.enable.network"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		sleep(4);
		Log.info("Validating that after network connection is resumed file uploading continues and successfull message is displayed");
		WebElement uploadCompleteElement = findElementSafe("main.xpath.upload.complete.msg.link");
		if(uploadCompleteElement==null){
			Log.info("in case uploading is not resumed, cancel the failed upload instead of waiting");
			WebElement uploadFailedClose = findElementSafe("main.xpath.upload.failed.msg.close");
			if(uploadFailedClose!=null){
				uploadFailedClose.click();
			}
		}
	}
	
	@AfterMethod
	public void afterMethod(){
		Log.endTestCase(testCaseName);
	}
	
	@AfterTest
	public void tearDown(){
		Log.info("Removing created files...");
		while (true){
			WebElement documentElement = findElementSafe("main.xpath.any.file");
			if(documentElement==null){
				break;
			}
			Log.info("Removing file...");
			try{
				Actions actions = new Actions(driver);
				actions.moveToElement(documentElement);
				actions.click();
				actions.sendKeys(Keys.DELETE);
				actions.perform();
			}
			catch(Exception ex){
				Log.info("Some issue with stale element, trying again...");
			}
		}

		Log.info("Closing the Browser....");
		try {
			Log.info("Enabling network connection, if disabled at this point");
			Runtime.getRuntime().exec(configProp.getProperty(os+".command.enable.network"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		driver.quit();
		Log.info("Browser Closed successfully....");
		Log.info("Test Completed, refer test.log ....");
	}

	private void sleep(int time){
		try {
			Thread.sleep(time *1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private WebElement findElementSafe(final By by, String timeout){
		WebElement webElement = null;
		int timeOut = Integer.parseInt(timeout);
		try{
			webElement = new WebDriverWait(driver, timeOut).until(new
					ExpectedCondition<WebElement>() {
				@Override
				public WebElement apply(WebDriver d) {
					return d.findElement(by);
				}
			});
		}
		catch(Exception ex) {
			Log.error("Find Element timed out for " + by.toString());
		}
		return webElement;
	}

	private WebElement findElementSafe(final By by){
		return findElementSafe(by, configProp.getProperty("default.explicit.wait"));
	}

	private By findLocatorToUse(String locator){
		return findLocatorToUse(locator,null);
	}

	private By findLocatorToUse(String locator, String fileType){
		By by = null;
		String[] arr = locator.split("\\.");
		String locatorType = arr[1].toUpperCase();
		String selector = locators.getProperty(locator);
		Log.info("Trying to find element with selector as : " + selector + " and locator Type as: " + locatorType);
		if(selector==null){
			Log.error("key-value is not present for key: " + locator);
			Assert.fail();
		}
		if(fileType!=null){
			if(selector.contains(configProp.getProperty("default." + fileType + ".name"))){
				String filePath = configProp.getProperty("main.upload." + fileType + ".path");
				String fileName = filePath.substring(filePath.indexOf('\\'));
				if(!fileName.equals(configProp.getProperty("default." + fileType + ".name"))){
					selector = selector.replaceAll(configProp.getProperty("default." + fileType + ".name"), fileName);
				}			
			}
		}
		switch(locatorType){
			case "ID" :
				by = By.id(selector);
				break;
			case "XPATH" :
				by = By.xpath(selector);
				break;
			case "NAME" :
				by = By.name(selector);
				break;
			case "CLASSNAME" :
				by = By.className(selector);
				break;
			case "CSSSELECTOR" :
				by = By.cssSelector(selector);
				break;
			case "LINKTEXT" :
				by = By.linkText(selector);
				break;
			case "PARTIALLINKTEXT" :
				by = By.partialLinkText(selector);
				break;
			case "TAGNAME" :
				by = By.tagName(selector);
				break;
			default :
				Log.error("locatorType: " + locatorType + " is not a valid type.");
				Assert.fail();
		}
		return by;
	}

	private WebElement findElementSafe(String locator){
		By by = findLocatorToUse(locator);
		return findElementSafe(by,configProp.getProperty("default.explicit.wait"));
	}

	private List<WebElement> findElementsSafe(By by){
		List<WebElement> webElements = null;
		findElementSafe(by);
		webElements = driver.findElements(by);
		return webElements;
	}

	private List<WebElement> findElementsSafe(String locator){
		List<WebElement> webElements = null;
		findElementSafe(locator);
		By by = findLocatorToUse(locator);
		webElements = driver.findElements(by);
		return webElements;
	}

	private List<WebElement> findElementsSafe(String locator, String fileType){
		List<WebElement> webElements = null;
		findElementSafe(locator);
		By by = findLocatorToUse(locator, fileType);
		webElements = driver.findElements(by);
		return webElements;
	}

	private Properties getProperties(String filename) {
		Log.info("reading the fiel : " + filename);
		Properties prop = new Properties();
		InputStream input = null;
		try {
			Log.info(this.getClass().getResource("/").getPath());
			Log.info(this.getClass().getResource(filename).getPath());
			input = this.getClass().getResourceAsStream(filename);
			if(input==null){
				Log.error("Sorry, unable to find " + filename);
				Assert.fail();
			}
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally{
			if(input!=null){
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;
	}
}