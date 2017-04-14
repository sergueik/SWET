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

```

org.openqa.selenium.WebDriverException: Failed to connect to binary FirefoxBinary(C:\Program Files (x86)\Mozilla Firefox\firefox.exe) on port 7055; process output follows: 
1492139582550	addons.manager	DEBUG	Application has been upgraded
1492139582722	addons.manager	DEBUG	Loaded provider scope for resource://gre/modules/addons/XPIProvider.jsm: ["XPIProvider"]
1492139582740	addons.manager	DEBUG	Loaded provider scope for resource://gre/modules/LightweightThemeManager.jsm: ["LightweightThemeManager"]
1492139582771	addons.manager	DEBUG	Loaded provider scope for resource://gre/modules/addons/GMPProvider.jsm
1492139582785	addons.manager	DEBUG	Loaded provider scope for resource://gre/modules/addons/PluginProvider.jsm
1492139582791	addons.manager	DEBUG	Starting provider: XPIProvider
1492139582793	addons.xpi	DEBUG	startup
1492139582798	addons.xpi	INFO	Mapping fxdriver@googlecode.com to C:\Users\ADMINI~1\AppData\Local\Temp\anonymous2107619804867418235webdriver-profile\extensions\fxdriver@googlecode.com
1492139582800	addons.xpi	DEBUG	Ignoring file entry whose name is not a valid add-on ID: C:\Users\ADMINI~1\AppData\Local\Temp\anonymous2107619804867418235webdriver-profile\extensions\webdriver-staging
1492139582802	addons.xpi	INFO	SystemAddonInstallLocation directory is missing
1492139582809	addons.xpi	INFO	Mapping aushelper@mozilla.org to C:\Program Files (x86)\Mozilla Firefox\browser\features\aushelper@mozilla.org.xpi
1492139582812	addons.xpi	INFO	Mapping deployment-checker@mozilla.org to C:\Program Files (x86)\Mozilla Firefox\browser\features\deployment-checker@mozilla.org.xpi
1492139582814	addons.xpi	INFO	Mapping e10srollout@mozilla.org to C:\Program Files (x86)\Mozilla Firefox\browser\features\e10srollout@mozilla.org.xpi
1492139582815	addons.xpi	INFO	Mapping firefox@getpocket.com to C:\Program Files (x86)\Mozilla Firefox\browser\features\firefox@getpocket.com.xpi
1492139582816	addons.xpi	INFO	Mapping webcompat@mozilla.org to C:\Program Files (x86)\Mozilla Firefox\browser\features\webcompat@mozilla.org.xpi
1492139582836	addons.xpi	INFO	Mapping {972ce4c6-7e08-4474-a285-3208198ce6fd} to C:\Program Files (x86)\Mozilla Firefox\browser\extensions\{972ce4c6-7e08-4474-a285-3208198ce6fd}.xpi
1492139582838	addons.xpi	DEBUG	Skipping unavailable install location app-system-share
1492139582839	addons.xpi	DEBUG	Skipping unavailable install location app-system-local
1492139582844	addons.xpi	DEBUG	checkForChanges
1492139582846	addons.xpi	INFO	SystemAddonInstallLocation directory is missing
1492139582851	addons.xpi	DEBUG	Loaded add-on state from prefs: {}
1492139582855	addons.xpi	DEBUG	New add-on fxdriver@googlecode.com in app-profile
1492139582859	addons.xpi	DEBUG	getModTime: Recursive scan of fxdriver@googlecode.com
1492139582888	addons.xpi	DEBUG	New add-on aushelper@mozilla.org in app-system-defaults
1492139582891	addons.xpi	DEBUG	getModTime: Recursive scan of aushelper@mozilla.org
1492139582894	addons.xpi	DEBUG	New add-on deployment-checker@mozilla.org in app-system-defaults
1492139582897	addons.xpi	DEBUG	getModTime: Recursive scan of deployment-checker@mozilla.org
1492139582899	addons.xpi	DEBUG	New add-on e10srollout@mozilla.org in app-system-defaults
1492139582901	addons.xpi	DEBUG	getModTime: Recursive scan of e10srollout@mozilla.org
1492139582903	addons.xpi	DEBUG	New add-on firefox@getpocket.com in app-system-defaults
1492139582907	addons.xpi	DEBUG	getModTime: Recursive scan of firefox@getpocket.com
1492139582909	addons.xpi	DEBUG	New add-on webcompat@mozilla.org in app-system-defaults
1492139582911	addons.xpi	DEBUG	getModTime: Recursive scan of webcompat@mozilla.org
1492139582914	addons.xpi	DEBUG	New add-on {972ce4c6-7e08-4474-a285-3208198ce6fd} in app-global
1492139582915	addons.xpi	DEBUG	getModTime: Recursive scan of {972ce4c6-7e08-4474-a285-3208198ce6fd}
1492139582917	addons.xpi	DEBUG	getInstallState changed: true, state: {"app-profile":{"fxdriver@googlecode.com":{"d":"C:\\Users\\ADMINI~1\\AppData\\Local\\Temp\\anonymous2107619804867418235webdriver-profile\\extensions\\fxdriver@googlecode.com","st":1492139576477,"mt":1492139576438}},"app-system-defaults":{"aushelper@mozilla.org":{"d":"C:\\Program Files (x86)\\Mozilla Firefox\\browser\\features\\aushelper@mozilla.org.xpi","st":1490296975000},"deployment-checker@mozilla.org":{"d":"C:\\Program Files (x86)\\Mozilla Firefox\\browser\\features\\deployment-checker@mozilla.org.xpi","st":1490296975000},"e10srollout@mozilla.org":{"d":"C:\\Program Files (x86)\\Mozilla Firefox\\browser\\features\\e10srollout@mozilla.org.xpi","st":1490296975000},"firefox@getpocket.com":{"d":"C:\\Program Files (x86)\\Mozilla Firefox\\browser\\features\\firefox@getpocket.com.xpi","st":1490296976000},"webcompat@mozilla.org":{"d":"C:\\Program Files (x86)\\Mozilla Firefox\\browser\\features\\webcompat@mozilla.org.xpi","st":1490296975000}},"app-global":{"{972ce4c6-7e08-4474-a285-3208198ce6fd}":{"d":"C:\\Program Files (x86)\\Mozilla Firefox\\browser\\extensions\\{972ce4c6-7e08-4474-a285-3208198ce6fd}.xpi","st":1490296975000}}}
1492139582979	addons.xpi-utils	DEBUG	Opening XPI database C:\Users\ADMINI~1\AppData\Local\Temp\anonymous2107619804867418235webdriver-profile\extensions.json
1492139582988	addons.xpi-utils	DEBUG	New add-on fxdriver@googlecode.com installed in app-profile
*** Blocklist::_loadBlocklistFromFile: blocklist is disabled
1492139583084	addons.xpi	WARN	Add-on fxdriver@googlecode.com is not correctly signed.
1492139583088	addons.xpi	WARN	Add-on fxdriver@googlecode.com is not correctly signed.
1492139583106	DeferredSave.extensions.json	DEBUG	Save changes
1492139583110	addons.xpi-utils	DEBUG	New add-on aushelper@mozilla.org installed in app-system-defaults
1492139583128	DeferredSave.extensions.json	DEBUG	Starting timer
1492139583143	DeferredSave.extensions.json	DEBUG	Save changes
1492139583144	addons.xpi-utils	DEBUG	New add-on deployment-checker@mozilla.org installed in app-system-defaults
1492139583175	DeferredSave.extensions.json	DEBUG	Save changes
1492139583182	addons.xpi-utils	DEBUG	New add-on e10srollout@mozilla.org installed in app-system-defaults
1492139583218	DeferredSave.extensions.json	DEBUG	Starting write
1492139583251	DeferredSave.extensions.json	DEBUG	Save changes
1492139583255	DeferredSave.extensions.json	DEBUG	Data changed while write in progress
1492139583261	addons.xpi-utils	DEBUG	New add-on firefox@getpocket.com installed in app-system-defaults
1492139583299	DeferredSave.extensions.json	DEBUG	Save changes
1492139583300	addons.xpi-utils	DEBUG	New add-on webcompat@mozilla.org installed in app-system-defaults
1492139583332	DeferredSave.extensions.json	DEBUG	Save changes
1492139583338	addons.xpi-utils	DEBUG	New add-on {972ce4c6-7e08-4474-a285-3208198ce6fd} installed in app-global
1492139583359	DeferredSave.extensions.json	DEBUG	Save changes
1492139583364	addons.manager	DEBUG	Registering startup change 'installed' for fxdriver@googlecode.com
1492139583365	addons.xpi-utils	DEBUG	Make addon app-profile:fxdriver@googlecode.com visible
1492139583368	DeferredSave.extensions.json	DEBUG	Save changes
1492139583371	addons.manager	DEBUG	Registering startup change 'installed' for aushelper@mozilla.org
1492139583440	addons.xpi	DEBUG	Loading bootstrap scope from C:\Program Files (x86)\Mozilla Firefox\browser\features\aushelper@mozilla.org.xpi
1492139583471	addons.xpi	DEBUG	Calling bootstrap method install on aushelper@mozilla.org version 2.0
1492139583472	addons.xpi-utils	DEBUG	Make addon app-system-defaults:aushelper@mozilla.org visible
1492139583475	DeferredSave.extensions.json	DEBUG	Save changes
1492139583476	addons.manager	DEBUG	Registering startup change 'installed' for deployment-checker@mozilla.org
1492139583479	addons.xpi	DEBUG	Loading bootstrap scope from C:\Program Files (x86)\Mozilla Firefox\browser\features\deployment-checker@mozilla.org.xpi
1492139583516	addons.xpi	DEBUG	Calling bootstrap method install on deployment-checker@mozilla.org version 1.0
1492139583664	addons.xpi-utils	DEBUG	Make addon app-system-defaults:deployment-checker@mozilla.org visible
1492139583666	DeferredSave.extensions.json	DEBUG	Save changes
1492139583668	addons.manager	DEBUG	Registering startup change 'installed' for e10srollout@mozilla.org
1492139583673	addons.xpi	DEBUG	Loading bootstrap scope from C:\Program Files (x86)\Mozilla Firefox\browser\features\e10srollout@mozilla.org.xpi
1492139583688	addons.xpi	DEBUG	Calling bootstrap method install on e10srollout@mozilla.org version 1.9
1492139583697	addons.xpi-utils	DEBUG	Make addon app-system-defaults:e10srollout@mozilla.org visible
1492139583702	DeferredSave.extensions.json	DEBUG	Save changes
1492139583704	addons.manager	DEBUG	Registering startup change 'installed' for firefox@getpocket.com
1492139583706	addons.xpi	DEBUG	Loading bootstrap scope from C:\Program Files (x86)\Mozilla Firefox\browser\features\firefox@getpocket.com.xpi
1492139583729	addons.xpi	DEBUG	Calling bootstrap method install on firefox@getpocket.com version 1.0.5
1492139583730	addons.xpi-utils	DEBUG	Make addon app-system-defaults:firefox@getpocket.com visible
1492139583732	DeferredSave.extensions.json	DEBUG	Save changes
1492139583734	addons.manager	DEBUG	Registering startup change 'installed' for webcompat@mozilla.org
1492139583736	addons.xpi	DEBUG	Loading bootstrap scope from C:\Program Files (x86)\Mozilla Firefox\browser\features\webcompat@mozilla.org.xpi
1492139583751	addons.xpi	DEBUG	Calling bootstrap method install on webcompat@mozilla.org version 1.0
1492139583752	addons.xpi-utils	DEBUG	Make addon app-system-defaults:webcompat@mozilla.org visible
1492139583753	DeferredSave.extensions.json	DEBUG	Save changes
1492139583755	addons.xpi-utils	DEBUG	Make addon app-global:{972ce4c6-7e08-4474-a285-3208198ce6fd} visible
1492139583756	DeferredSave.extensions.json	DEBUG	Save changes
1492139583760	addons.xpi	DEBUG	Updating XPIState for {"id":"fxdriver@googlecode.com","syncGUID":"{6a79ac90-4916-4c10-9c97-95036009bbe0}","location":"app-profile","version":"3.2.0","type":"extension","internalName":null,"updateURL":null,"updateKey":null,"optionsURL":null,"optionsType":null,"aboutURL":null,"icons":{},"iconURL":null,"icon64URL":null,"defaultLocale":{"name":"Firefox WebDriver","description":"WebDriver implementation for Firefox","creator":"Simon Stewart","homepageURL":null},"visible":true,"active":false,"userDisabled":false,"appDisabled":true,"descriptor":"C:\\Users\\ADMINI~1\\AppData\\Local\\Temp\\anonymous2107619804867418235webdriver-profile\\extensions\\fxdriver@googlecode.com","installDate":1492139576477,"updateDate":1492139576477,"applyBackgroundUpdates":1,"bootstrap":false,"skinnable":false,"size":3267161,"sourceURI":null,"releaseNotesURI":null,"softDisabled":false,"foreignInstall":true,"hasBinaryComponents":false,"strictCompatibility":false,"locales":[],"targetApplications":[{"id":"{ec8030f7-c20a-464f-9b0e-13a3a9e97384}","minVersion":"3.0","maxVersion":"48.0"}],"targetPlatforms":[{"os":"Darwin","abi":null},{"os":"SunOS","abi":null},{"os":"FreeBSD","abi":null},{"os":"OpenBSD","abi":null},{"os":"WINNT","abi":null},{"os":"Linux","abi":null}],"multiprocessCompatible":false,"signedState":0,"seen":true,"dependencies":[],"hasEmbeddedWebExtension":false,"mpcOptedOut":false}
1492139583762	addons.xpi	DEBUG	Updating XPIState for {"id":"aushelper@mozilla.org","syncGUID":"{215cda44-33c4-471d-a8e1-e14cc5eb4a4b}","location":"app-system-defaults","version":"2.0","type":"extension","internalName":null,"updateURL":null,"updateKey":null,"optionsURL":null,"optionsType":null,"aboutURL":null,"icons":{},"iconURL":null,"icon64URL":null,"defaultLocale":{"name":"Application Update Service Helper","description":"Sets value(s) in the update url based on custom checks.","creator":null,"homepageURL":null},"visible":true,"active":true,"userDisabled":false,"appDisabled":false,"descriptor":"C:\\Program Files (x86)\\Mozilla Firefox\\browser\\features\\aushelper@mozilla.org.xpi","installDate":1490296975000,"updateDate":1490296975000,"applyBackgroundUpdates":1,"bootstrap":true,"skinnable":false,"size":8488,"sourceURI":null,"releaseNotesURI":null,"softDisabled":false,"foreignInstall":false,"hasBinaryComponents":false,"strictCompatibility":false,"locales":[],"targetApplications":[{"id":"{ec8030f7-c20a-464f-9b0e-13a3a9e97384}","minVersion":"52.0.2","maxVersion":"52.*"}],"targetPlatforms":[],"multiprocessCompatible":true,"seen":true,"dependencies":[],"hasEmbeddedWebExtension":false,"mpcOptedOut":false}
1492139583763	addons.xpi	DEBUG	getModTime: Recursive scan of aushelper@mozilla.org
1492139583766	addons.xpi	DEBUG	Updating XPIState for {"id":"deployment-checker@mozilla.org","syncGUID":"{62e38d05-c572-4142-ae67-9627b61142a1}","location":"app-system-defaults","version":"1.0","type":"extension","internalName":null,"updateURL":null,"updateKey":null,"optionsURL":null,"optionsType":null,"aboutURL":null,"icons":{},"iconURL":null,"icon64URL":null,"defaultLocale":{"name":"Site Deployment Checker","description":"Check that Users Encounter Mozilla Sites as Deployed by Mozilla","creator":null,"homepageURL":null},"visible":true,"active":true,"userDisabled":false,"appDisabled":false,"descriptor":"C:\\Program Files (x86)\\Mozilla Firefox\\browser\\features\\deployment-checker@mozilla.org.xpi","installDate":1490296975000,"updateDate":1490296975000,"applyBackgroundUpdates":1,"bootstrap":true,"skinnable":false,"size":11555,"sourceURI":null,"releaseNotesURI":null,"softDisabled":false,"foreignInstall":false,"hasBinaryComponents":false,"strictCompatibility":false,"locales":[],"targetApplications":[{"id":"{ec8030f7-c20a-464f-9b0e-13a3a9e97384}","minVersion":"52.0.2","maxVersion":"52.*"}],"targetPlatforms":[],"multiprocessCompatible":true,"seen":true,"dependencies":[],"hasEmbeddedWebExtension":false,"mpcOptedOut":false}
1492139583768	addons.xpi	DEBUG	getModTime: Recursive scan of deployment-checker@mozilla.org
1492139583770	addons.xpi	DEBUG	Updating XPIState for {"id":"e10srollout@mozilla.org","syncGUID":"{65d43247-6e5e-4742-98cd-e0523c9795d8}","location":"app-system-defaults","version":"1.9","type":"extension","internalName":null,"updateURL":null,"updateKey":null,"optionsURL":null,"optionsType":null,"aboutURL":null,"icons":{},"iconURL":null,"icon64URL":null,"defaultLocale":{"name":"Multi-process staged rollout","description":"Staged rollout of Firefox multi-process feature.","creator":null,"homepageURL":null},"visible":true,"active":true,"userDisabled":false,"appDisabled":false,"descriptor":"C:\\Program Files (x86)\\Mozilla Firefox\\browser\\features\\e10srollout@mozilla.org.xpi","installDate":1490296975000,"updateDate":1490296975000,"applyBackgroundUpdates":1,"bootstrap":true,"skinnable":false,"size":7183,"sourceURI":null,"releaseNotesURI":null,"softDisabled":false,"foreignInstall":false,"hasBinaryComponents":false,"strictCompatibility":false,"locales":[],"targetApplications":[{"id":"{ec8030f7-c20a-464f-9b0e-13a3a9e97384}","minVersion":"52.0.2","maxVersion":"52.*"}],"targetPlatforms":[],"multiprocessCompatible":true,"seen":true,"dependencies":[],"hasEmbeddedWebExtension":false,"mpcOptedOut":false}
1492139583771	addons.xpi	DEBUG	getModTime: Recursive scan of e10srollout@mozilla.org
1492139583774	addons.xpi	DEBUG	Updating XPIState for {"id":"firefox@getpocket.com","syncGUID":"{cd68dcfa-af81-453d-8e60-d294ffb3eeb2}","location":"app-system-defaults","version":"1.0.5","type":"extension","internalName":null,"updateURL":null,"updateKey":null,"optionsURL":null,"optionsType":null,"aboutURL":null,"icons":{},"iconURL":null,"icon64URL":null,"defaultLocale":{"name":"Pocket","description":"When you find something you want to view later, put it in Pocket.","creator":null,"homepageURL":null},"visible":true,"active":true,"userDisabled":false,"appDisabled":false,"descriptor":"C:\\Program Files (x86)\\Mozilla Firefox\\browser\\features\\firefox@getpocket.com.xpi","installDate":1490296976000,"updateDate":1490296976000,"applyBackgroundUpdates":1,"bootstrap":true,"skinnable":false,"size":920281,"sourceURI":null,"releaseNotesURI":null,"softDisabled":false,"foreignInstall":false,"hasBinaryComponents":false,"strictCompatibility":false,"locales":[],"targetApplications":[{"id":"{ec8030f7-c20a-464f-9b0e-13a3a9e97384}","minVersion":"52.0.2","maxVersion":"52.*"}],"targetPlatforms":[],"multiprocessCompatible":true,"seen":true,"dependencies":[],"hasEmbeddedWebExtension":false,"mpcOptedOut":false}
1492139583776	addons.xpi	DEBUG	getModTime: Recursive scan of firefox@getpocket.com
1492139583779	addons.xpi	DEBUG	Updating XPIState for {"id":"webcompat@mozilla.org","syncGUID":"{f558e22d-346c-4bce-b3c5-15936ab73883}","location":"app-system-defaults","version":"1.0","type":"extension","internalName":null,"updateURL":null,"updateKey":null,"optionsURL":null,"optionsType":null,"aboutURL":null,"icons":{},"iconURL":null,"icon64URL":null,"defaultLocale":{"name":"Web Compat","description":"Urgent post-release fixes for web compatibility.","creator":null,"homepageURL":null},"visible":true,"active":true,"userDisabled":false,"appDisabled":false,"descriptor":"C:\\Program Files (x86)\\Mozilla Firefox\\browser\\features\\webcompat@mozilla.org.xpi","installDate":1490296975000,"updateDate":1490296975000,"applyBackgroundUpdates":1,"bootstrap":true,"skinnable":false,"size":1456,"sourceURI":null,"releaseNotesURI":null,"softDisabled":false,"foreignInstall":false,"hasBinaryComponents":false,"strictCompatibility":false,"locales":[],"targetApplications":[{"id":"{ec8030f7-c20a-464f-9b0e-13a3a9e97384}","minVersion":"52.0.2","maxVersion":"52.*"}],"targetPlatforms":[],"multiprocessCompatible":true,"seen":true,"dependencies":[],"hasEmbeddedWebExtension":false,"mpcOptedOut":false}
1492139583780	addons.xpi	DEBUG	getModTime: Recursive scan of webcompat@mozilla.org
1492139583783	addons.xpi	DEBUG	Updating XPIState for {"id":"{972ce4c6-7e08-4474-a285-3208198ce6fd}","syncGUID":"{8079c361-99ab-4394-832e-f593d3297873}","location":"app-global","version":"52.0.2","type":"theme","internalName":"classic/1.0","updateURL":null,"updateKey":null,"optionsURL":null,"optionsType":null,"aboutURL":null,"icons":{"32":"icon.png","48":"icon.png"},"iconURL":null,"icon64URL":null,"defaultLocale":{"name":"Default","description":"The default theme.","creator":"Mozilla","homepageURL":null,"contributors":["Mozilla Contributors"]},"visible":true,"active":true,"userDisabled":false,"appDisabled":false,"descriptor":"C:\\Program Files (x86)\\Mozilla Firefox\\browser\\extensions\\{972ce4c6-7e08-4474-a285-3208198ce6fd}.xpi","installDate":1490296975000,"updateDate":1490296975000,"applyBackgroundUpdates":1,"skinnable":true,"size":19587,"sourceURI":null,"releaseNotesURI":null,"softDisabled":false,"foreignInstall":false,"hasBinaryComponents":false,"strictCompatibility":true,"locales":[],"targetApplications":[{"id":"{ec8030f7-c20a-464f-9b0e-13a3a9e97384}","minVersion":"52.0.2","maxVersion":"52.0.2"}],"targetPlatforms":[],"seen":true,"dependencies":[],"hasEmbeddedWebExtension":false}
1492139583786	addons.xpi	DEBUG	getModTime: Recursive scan of {972ce4c6-7e08-4474-a285-3208198ce6fd}
1492139583800	DeferredSave.extensions.json	DEBUG	Save changes
1492139583805	addons.xpi	DEBUG	Updating database with changes to installed add-ons
1492139583810	addons.xpi-utils	DEBUG	Updating add-on states
1492139583817	addons.xpi-utils	DEBUG	Writing add-ons list
1492139583831	addons.xpi	DEBUG	Registering manifest for C:\Program Files (x86)\Mozilla Firefox\browser\features\aushelper@mozilla.org.xpi
1492139583835	addons.xpi	DEBUG	Calling bootstrap method startup on aushelper@mozilla.org version 2.0
1492139583843	addons.xpi	DEBUG	Registering manifest for C:\Program Files (x86)\Mozilla Firefox\browser\features\deployment-checker@mozilla.org.xpi
1492139583848	addons.xpi	DEBUG	Calling bootstrap method startup on deployment-checker@mozilla.org version 1.0
1492139583852	addons.xpi	DEBUG	Registering manifest for C:\Program Files (x86)\Mozilla Firefox\browser\features\e10srollout@mozilla.org.xpi
1492139583855	addons.xpi	DEBUG	Calling bootstrap method startup on e10srollout@mozilla.org version 1.9
1492139583889	addons.xpi	DEBUG	Registering manifest for C:\Program Files (x86)\Mozilla Firefox\browser\features\firefox@getpocket.com.xpi
1492139583895	addons.xpi	DEBUG	Calling bootstrap method startup on firefox@getpocket.com version 1.0.5
1492139583903	addons.xpi	DEBUG	Registering manifest for C:\Program Files (x86)\Mozilla Firefox\browser\features\webcompat@mozilla.org.xpi
1492139583906	addons.xpi	DEBUG	Calling bootstrap method startup on webcompat@mozilla.org version 1.0
1492139583923	addons.manager	DEBUG	Registering shutdown blocker for XPIProvider
1492139583924	addons.manager	DEBUG	Provider finished startup: XPIProvider
1492139583925	addons.manager	DEBUG	Starting provider: LightweightThemeManager
1492139583925	addons.manager	DEBUG	Registering shutdown blocker for LightweightThemeManager
1492139583927	addons.manager	DEBUG	Provider finished startup: LightweightThemeManager
1492139583930	addons.manager	DEBUG	Starting provider: GMPProvider
1492139583989	addons.manager	DEBUG	Registering shutdown blocker for GMPProvider
1492139583992	addons.manager	DEBUG	Provider finished startup: GMPProvider
1492139583992	addons.manager	DEBUG	Starting provider: PluginProvider
1492139583994	addons.manager	DEBUG	Registering shutdown blocker for PluginProvider
1492139584005	addons.manager	DEBUG	Provider finished startup: PluginProvider
1492139584009	addons.manager	DEBUG	Completed startup sequence
1492139586539	addons.manager	DEBUG	Starting provider: <unnamed-provider>
1492139586540	addons.manager	DEBUG	Registering shutdown blocker for <unnamed-provider>
1492139586542	addons.manager	DEBUG	Provider finished startup: <unnamed-provider>
1492139596453	DeferredSave.extensions.json	DEBUG	Write succeeded
1492139596454	addons.xpi-utils	DEBUG	XPI Database saved, setting schema version preference to 19
1492139596455	DeferredSave.extensions.json	DEBUG	Starting timer
1492139596580	DeferredSave.extensions.json	DEBUG	Starting write
1492139596843	addons.repository	DEBUG	No addons.json found.
1492139596846	DeferredSave.addons.json	DEBUG	Save changes
1492139596862	DeferredSave.addons.json	DEBUG	Starting timer
1492139597068	addons.manager	DEBUG	Starting provider: PreviousExperimentProvider
1492139597073	addons.manager	DEBUG	Registering shutdown blocker for PreviousExperimentProvider
1492139597075	addons.manager	DEBUG	Provider finished startup: PreviousExperimentProvider
1492139597145	DeferredSave.addons.json	DEBUG	Starting write
1492139603698	DeferredSave.extensions.json	DEBUG	Write succeeded
1492139607064	DeferredSave.addons.json	DEBUG	Write succeeded

Build info: version: '3.2.0', revision: '8c03df6b79', time: '2017-02-23 10:51:31 +0000'
System info: host: 'WIN-KDTC5E27F3K', ip: '10.0.2.15', os.name: 'Windows Server 2012 R2', os.arch: 'amd64', os.version: '6.3', java.version: '1.8.0_101'
Driver info: driver.version: BrowserDriver
	at org.openqa.selenium.firefox.internal.NewProfileExtensionConnection.start(NewProfileExtensionConnection.java:124)
	at org.openqa.selenium.firefox.FirefoxDriver.startClient(FirefoxDriver.java:382)
	at org.openqa.selenium.remote.RemoteWebDriver.<init>(RemoteWebDriver.java:119)
	at org.openqa.selenium.firefox.FirefoxDriver.<init>(FirefoxDriver.java:293)
	at org.openqa.selenium.firefox.FirefoxDriver.<init>(FirefoxDriver.java:272)
	at org.openqa.selenium.firefox.FirefoxDriver.<init>(FirefoxDriver.java:267)
	at org.openqa.selenium.firefox.FirefoxDriver.<init>(FirefoxDriver.java:130)
	at org.swet.BrowserDriver.initialize(BrowserDriver.java:69)
	at org.swet.SwetTest.beforeSuiteMethod(SwetTest.java:108)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at org.junit.internal.runners.statements.RunBefores.evaluate(RunBefores.java:24)
	at org.junit.internal.runners.statements.RunAfters.evaluate(RunAfters.java:27)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:252)
	at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:141)
	at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:112)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.apache.maven.surefire.util.ReflectionUtils.invokeMethodWithArray(ReflectionUtils.java:189)
	at org.apache.maven.surefire.booter.ProviderFactory$ProviderProxy.invoke(ProviderFactory.java:165)
	at org.apache.maven.surefire.booter.ProviderFactory.invokeProvider(ProviderFactory.java:85)
	at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:115)
	at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:75)
Caused by: org.openqa.selenium.firefox.NotConnectedException: Unable to connect to host 127.0.0.1 on port 7055 after 45000 ms. Firefox console output:
1492139582550	addons.manager	DEBUG	Application has been upgraded

```
possibly related to https://github.com/SeleniumHQ/selenium/issues/3630 and 
https://github.com/mozilla/geckodriver/issues/524