# 0.0.5

## 04/11/2017

## Features

- Moved the code  into separate project on github 
- Created branch selenium_3x to stabilize with latest Selenium 3 code.

## Issues

Exception on Windows x64 / Selenium 3.0.1 / Firefox 52 / geckoDriver 0.15 with capabilities.setCapability("marionette", true);:
```
Unable to create new remote session. desired capabilities = 
Capabilities [{firefox_binary=C:\Program Files\Mozilla Firefox\firefox.exe, 
elementScrollBehavior=1, marionette=true, firefoxOptions=org.openqa.selenium.firefox.FirefoxOptions@60611244, 
browserName=firefox, moz:firefoxOptions=org.openqa.selenium.firefox.FirefoxOptions@60611244, 
version=, platform=ANY, 
firefox_profile=org.openqa.selenium.firefox.F...}], 
required capabilities = Capabilities [{}]
Build info: version: '3.0.1', revision: '1969d75', time: '2016-10-18 09:49:13 -0700'
System info: host: 'WIN-KDTC5E27F3K', ip: '10.0.2.15', os.name: 'Windows Server 2012 R2', 
os.arch: 'amd64', os.version: '6.3', java.version: '1.8.0_101'
Driver info: driver.version: BrowserDriver
  org.openqa.selenium.remote.ProtocolHandshake.createSession(ProtocolHandshake.java:91)
  org.openqa.selenium.remote.HttpCommandExecutor.execute(HttpCommandExecutor.java:141)
  org.openqa.selenium.remote.service.DriverCommandExecutor.execute(DriverCommandExecutor.java:82)
  org.openqa.selenium.remote.RemoteWebDriver.execute(RemoteWebDriver.java:601)
  org.openqa.selenium.remote.RemoteWebDriver.startSession(RemoteWebDriver.java:241)
  org.openqa.selenium.remote.RemoteWebDriver.<init>(RemoteWebDriver.java:128)
  org.openqa.selenium.firefox.FirefoxDriver.<init>(FirefoxDriver.java:259)
  org.openqa.selenium.firefox.FirefoxDriver.<init>(FirefoxDriver.java:247)
  org.openqa.selenium.firefox.FirefoxDriver.<init>(FirefoxDriver.java:242)
  org.openqa.selenium.firefox.FirefoxDriver.<init>(FirefoxDriver.java:135)
  org.swet.BrowserDriver.initialize(BrowserDriver.java:96)
  // 			driver = new FirefoxDriver(capabilities);
```
possibly related to http://stackoverflow.com/questions/40106844/selenium-3-0-firefx-driver-fails-with-org-openqa-selenium-sessionnotcreatedexcep

the error with capabilities.setCapability("marionette", false);
becomes
```
Failed to connect to binary FirefoxBinary(C:\Program Files (x86)\Mozilla Firefox\firefox.exe) 
Unable to connect to host 127.0.0.1 on port 7055 after 45000 ms. Firefox console output:
\extensions\\{972ce4c6-7e08-4474-a285-3208198ce6fd}.xpi","installDate":1490296975000,"updateDate":1490296975000,"applyBackgroundUpdates":1,"skinnable":true,"size":19587,"sourceURI":null,"releaseNotesURI":null,"softDisabled":false,"foreignInstall":false,"hasBinaryComponents":false,"strictCompatibility":true,"locales":[],"targetApplications":[{"id":"{ec8030f7-c20a-464f-9b0e-13a3a9e97384}","minVersion":"52.0.2","maxVersion":"52.0.2"}],"targetPlatforms":[],"seen":true,"dependencies":[],"hasEmbeddedWebExtension":false}
1491971724867	addons.xpi	DEBUG	getModTime: Recursive scan of {972ce4c6-7e08-4474-a285-3208198ce6fd}
1491971724874	DeferredSave.extensions.json	DEBUG	Save changes
1491971724875	addons.xpi	DEBUG	Updating database with changes to installed add-ons
1491971724878	addons.xpi-utils	DEBUG	Updating add-on states
1491971724884	addons.xpi-utils	DEBUG	Writing add-ons list
1491971724893	addons.xpi	DEBUG	Registering manifest for C:\Program Files (x86)\Mozilla Firefox\browser\features\aushelper@mozilla.org.xpi
1491971724895	addons.xpi	DEBUG	Calling bootstrap method startup on aushelper@mozilla.org version 2.0
1491971724899	addons.xpi	DEBUG	Registering manifest for C:\Program Files (x86)\Mozilla Firefox\browser\features\deployment-checker@mozilla.org.xpi
1491971724901	addons.xpi	DEBUG	Calling bootstrap method startup on deployment-checker@mozilla.org version 1.0
1491971724903	addons.xpi	DEBUG	Registering manifest for C:\Program Files (x86)\Mozilla Firefox\browser\features\e10srollout@mozilla.org.xpi
1491971724904	addons.xpi	DEBUG	Calling bootstrap method startup on e10srollout@mozilla.org version 1.9
1491971724906	addons.xpi	DEBUG	Registering manifest for C:\Program Files (x86)\Mozilla Firefox\browser\features\firefox@getpocket.com.xpi
1491971724908	addons.xpi	DEBUG	Calling bootstrap method startup on firefox@getpocket.com version 1.0.5
1491971724921	addons.xpi	DEBUG	Registering manifest for C:\Program Files (x86)\Mozilla Firefox\browser\features\webcompat@mozilla.org.xpi
1491971724923	addons.xpi	DEBUG	Calling bootstrap method startup on webcompat@mozilla.org version 1.0
1491971724930	addons.manager	DEBUG	Registering shutdown blocker for XPIProvider
1491971724931	addons.manager	DEBUG	Provider finished startup: XPIProvider
1491971724931	addons.manager	DEBUG	Starting provider: LightweightThemeManager
1491971724931	addons.manager	DEBUG	Registering shutdown blocker for LightweightThemeManager
1491971724932	addons.manager	DEBUG	Provider finished startup: LightweightThemeManager
1491971724933	addons.manager	DEBUG	Starting provider: GMPProvider
1491971724965	addons.manager	DEBUG	Registering shutdown blocker for GMPProvider
1491971724966	addons.manager	DEBUG	Provider finished startup: GMPProvider
1491971724966	addons.manager	DEBUG	Starting provider: PluginProvider
1491971724967	addons.manager	DEBUG	Registering shutdown blocker for PluginProvider
1491971724967	addons.manager	DEBUG	Provider finished startup: PluginProvider
1491971724969	addons.manager	DEBUG	Completed startup sequence
1491971725767	DeferredSave.extensions.json	DEBUG	Starting write
1491971725793	addons.manager	DEBUG	Starting provider: <unnamed-provider>
1491971725794	addons.manager	DEBUG	Registering shutdown blocker for <unnamed-provider>
1491971725794	addons.manager	DEBUG	Provider finished startup: <unnamed-provider>
1491971729722	addons.repository	DEBUG	No addons.json found.
1491971729723	DeferredSave.addons.json	DEBUG	Save changes
1491971729734	DeferredSave.addons.json	DEBUG	Starting timer
1491971729812	addons.manager	DEBUG	Starting provider: PreviousExperimentProvider
1491971729813	addons.manager	DEBUG	Registering shutdown blocker for PreviousExperimentProvider
1491971729814	addons.manager	DEBUG	Provider finished startup: PreviousExperimentProvider
1491971729835	DeferredSave.extensions.json	DEBUG	Write succeeded
1491971729837	addons.xpi-utils	DEBUG	XPI Database saved, setting schema version preference to 19
1491971729839	DeferredSave.addons.json	DEBUG	Starting write
1491971732364	DeferredSave.addons.json	DEBUG	Write succeeded

  org.openqa.selenium.firefox.internal.NewProfileExtensionConnection.start(NewProfileExtensionConnection.java:113)
  org.openqa.selenium.firefox.FirefoxDriver.startClient(FirefoxDriver.java:347)
  org.openqa.selenium.remote.RemoteWebDriver.<init>(RemoteWebDriver.java:116)
  org.openqa.selenium.firefox.FirefoxDriver.<init>(FirefoxDriver.java:259)
  org.openqa.selenium.firefox.FirefoxDriver.<init>(FirefoxDriver.java:247)
  org.openqa.selenium.firefox.FirefoxDriver.<init>(FirefoxDriver.java:242)
  org.openqa.selenium.firefox.FirefoxDriver.<init>(FirefoxDriver.java:135)
  org.swet.BrowserDriver.initialize(BrowserDriver.java:96)

```

possibly related to https://github.com/SeleniumHQ/selenium/issues/2411