
### Info

![icon](https://github.com/sergueik/SWET/blob/master/src/main/resources/images/document_wrench_color.png)

__Selenium WebDriver Elementor Toolkit__  ( __SWET__  = light , in Russian) is a OS-independent successor to the [Selenium WebDriver Page Recorder](https://github.com/dzharii/swd-recorder) (__SWD__)of
Dmytro Zharii and author. __SWET__ is using the
[Eclipse Standard Widget Toolkit](https://www.eclipse.org/swt/) with third party [Opal](https://github.com/lcaron/opal) widget library'
instead of Microsoft .Net Windows Forms for user interface and [Jtwig](http://jtwig.org/documentation/reference/tags/control-flow) template engine instead of [ASP.Net Razor](https://en.wikipedia.org/wiki/ASP.NET_Razor) for code generation (that is just one of the available template exngines - note, __jtwig__ supports the original [PHP Twig](http://twig.sensiolabs.org/doc/2.x/) syntax). __SWET__ also supports generating the __keyword driven framework__ in e.g. Excel spreadsheet. This is a work in progress, since each keyword driven framework has its own
 list of *keywords* it recognizes.


Therefore __SWET__ runs on Windows, Mac or Linux, 32 or 64 bit platforms.
__SWET__ is currently beta quality: one can create a session and ealuate and save Page Element information and convert it to a
fragment of the code in the Java or C# language; eventually the full functionality of __SWD__ is to be achieved, also __SWET__ might become an [Eclipse plugin](http://www.vogella.com/tutorials/EclipsePlugin/article.html).

The application is being developed in Ecipse with [SWT Designer/Window Builder](http://www.vogella.com/tutorials/EclipseWindowBuilder/article.html),
on Ubuntu 16.04 and Windows.
For Mac / Safari testing, the [Sierra Final 10.12](https://techsviewer.com/install-macos-sierra-virtualbox-windows/) Virtual Box by TechReviews is being used.
Currently, working with Safari browser is somewhat flaky.

The Virtualbox images are setup for Selenium 3.x testing (work in progress).

![OSX Example](https://github.com/sergueik/SWET/blob/master/screenshots/capture1.png)

![Ubuntu Example](https://github.com/sergueik/SWET/blob/master/screenshots/capture2.png)

![Windows Example](https://github.com/sergueik/SWET/blob/master/screenshots/capture3.png)

### Usage

In order to use __SWET__ one will need to compile the application jar from the source - it is not difficult.
Continue reading for info on how to get the dev environment setup.

### Prerequisites
The project can be compiled and run from Eclipse or standalone.
To build the project outside of Eclipse, JDK 1.8 or later and Maven need to be installed and in the `PATH`. There launcher  scripts is  explained below.
In the checked-in runner scripts, the Java and Maven were conveniently installed to `c:\java\` for Windows.

On the Mac, the
JDK is expected to be installed to
`/Library/Java/JavaVirtualMachines/jdk$JAVA_VERSION.jdk/Contents/Home` which is the default location.

With the exception of one jar, the project dependencies are pulled by Maven (NOTE: this is being changed towards having no non-maven dependencies).

#### Updating the platform-specific information in the `pom.xml`

The project `pom.xml` currently is uing Maven prifiles to make selection of the main `swt.jar` dependency in a platform-specific fashion:

```xml
    <profile>
      <id>windows64</id>
      <activation>
        <os>
          <family>dos</family>
          <arch>amd64</arch>
        </os>
      </activation>
      <properties>
        <eclipse.swt.artifactId>org.eclipse.swt.win32.win32.x86_64</eclipse.swt.artifactId>
        <build_os>win64</build_os>
      </properties>
    </profile>
    <profile>
      <id>mac64</id>
      <activation>
        <os>
          <family>mac</family>
          <arch>x86_64</arch>
        </os>
      </activation>
      <properties>
        <build_os>mac64</build_os>
        <eclipse.swt.artifactId>org.eclipse.swt.cocoa.macosx.x86_64</eclipse.swt.artifactId>
      </properties>
    </profile>
    <profile>
      <id>unix32</id>
      <activation>
        <os>
          <family>unix</family>
          <arch>i386</arch>
        </os>
      </activation>
      <properties>
        <build_os>unix32</build_os>
        <selenium.version>2.53.1</selenium.version>
        <eclipse.swt.artifactId>org.eclipse.swt.gtk.linux.x86</eclipse.swt.artifactId>
      </properties>
    </profile>
    <profile>
      <id>unix64</id>
      <activation>
        <os>
          <family>unix</family>
          <arch>amd64</arch>
        </os>
      </activation>
      <properties>
        <build_os>unix64</build_os>
        <eclipse.swt.artifactId>org.eclipse.swt.gtk.linux.x86_64</eclipse.swt.artifactId>
      </properties>
    </profile>
```
The correct profile is selected automatically. If this fails to work for some reason, one will need to copy the relevant `artifactId` property definition into the
```xml
  <properties>
    <eclipse.swt.version>4.3</eclipse.swt.version>
    <eclipse.swt.artifactId>org.eclipse.swt.win32.win32.x86_64</eclipse.swt.artifactId>
  </properties>
  <dependencies>
    <dependency>
			<groupId>org.eclipse.swt</groupId>
			<artifactId>${eclipse.swt.artifactId}</artifactId>
      <version>${eclipse.swt.version}</version>
		</dependency>
    ...
```

Due to some problem with JVM loader, these platform-dependent jars cannot be included simultaneously.
The alternative is to package the spring-boot jar file as explained in
[Multiplatform SWT](https://github.com/jendap/multiplatform-swt) project.
Unfortulately the resulting bare-bones `multiplatform-swt-loader.jar` file is almost 10 Mb and with all dependencies the
__SWET__ jar is over 30 Mb which is not very practical.
Therefore,  we recommend to modify the `pom.xml` and use runner scripts to compile it as explained below.

#### Runner Scripts
After the project is cloned or downloaded from from github, one will find the following `run.*` scripts helpful to compile and start the application:
On Windows, use either Powershell script
```powershell
. .\run.ps1
```
or a batch file
```cmd
run.cmd
```
On Umix /Mac, run Bash script
```bash
./run.sh
```

The script will download the dependency jar(s), that is(are) not hosted on Maven Central repository,
compile and package the project using maven
and run the application jar from the `target` directory.

The script configuration has to be updated with the actual paths to Java and Maven:
```powershell
$MAVEN_VERSION = '3.3.9'
$JAVA_VERSION = '1.8.0_101'
$env:JAVA_HOME = "c:\java\jdk${JAVA_VERSION}"
$env:M2_HOME = "c:\java\apache-maven-${MAVEN_VERSION}"
$env:M2 = "${env:M2_HOME}\bin"
```

```cmd
if "%JAVA_VERSION%"=="" set JAVA_VERSION=1.8.0_101
set JAVA_HOME=c:\java\jdk%JAVA_VERSION%
if "%MAVEN_VERSION%"=="" set MAVEN_VERSION=3.3.9
set M2_HOME=c:\java\apache-maven-%MAVEN_VERSION%

```
```bash
JAVA_VERSION='1.8.0_121'
MAVEN_VERSION='3.3.9'
```

After the project compiled once, it can be run the jar through the command (assuming java is in the `PATH`):
```cmd
java.exe -cp target\swet-0.0.5-SNAPSHOT.jar;target\lib\* org.swet.SimpleToolBarEx
```
or

```bash
java.exe -cp target/swet-0.0.5-SNAPSHOT.jar:target/lib/* org.swet.SimpleToolBarEx
```
- without changing to the source, there is little reason to recompile it every time. Adding the `META-INF/MANIFEST.MF`
is a work in progress.


### Recording the page elements with SWET.

#### Toolbar Buttons

![launch](https://github.com/sergueik/SWET/blob/master/src/main/resources/images/launch_32.png)
launches the browser

![launch](https://github.com/sergueik/SWET/blob/master/src/main/resources/images/find_32.png)
injects the [SWD Element Searcher script](https://github.com/sergueik/SWET/blob/master/src/main/resources/ElementSearch.js)
into the page, then starts polling the page waiting for user to select some element via right click and to fill and submit the form:
![SWD Table](https://github.com/sergueik/SWET/blob/master/screenshots/swd_table.png)

The Java reads back the result once it available and adds a breadcrump button:
![breadcumps](https://github.com/sergueik/SWET/blob/master/screenshots/breadcumps.png)

The breadcrump button opens the form dialog with the details of the element:
![form](https://github.com/sergueik/SWET/blob/master/screenshots/form.png)

The save and load buttons
![flowchart](https://github.com/sergueik/SWET/blob/master/src/main/resources/images/save_32.png)
![flowchart](https://github.com/sergueik/SWET/blob/master/src/main/resources/images/open_32.png)
save  and restore the test session in YAML format.
![flowchart](https://github.com/sergueik/SWET/blob/master/screenshots/open_sesssion.png)

The code generation button
![flowchart](https://github.com/sergueik/SWET/blob/master/src/main/resources/images/codegen_32.png)

starts code generation using [Jtwig](http://jtwig.org/) tempate and `elementData` hash and opens result in a separate dialog:
![codegen](https://github.com/sergueik/SWET/blob/master/screenshots/codegen.png)

The preferences button
![preferences](https://github.com/sergueik/SWET/blob/master/src/main/resources/images/gear_36.png)
opens the configuration dialog
![config](https://github.com/sergueik/SWET/blob/master/screenshots/config.png)
Currently the browser and template selection are configurable, one also can set the base URL.

Currently project is hardcoded to start using Chrome browser on Windows, Safari on Mac and Firefox on the rest of platforms -  browser choice is configurable.
Saving / loading the YAML configuration file is a work in progress.
Eventually other common formats: YAML, JSON, POI or Java properties file - will be supported.

#### Operation
Both __SWD__ and __SWET__ inject certain Javascript code `ElementSearch.js` into the page, that the user can interct with with the mouse right-click.
After injecting the script the IDE waits polling for the speficic
`document.swdpr_command` object to be present on that page. This object is created  by the `ElementSearch.js`
when user selects the specific element on the page he is interested to access in the test script,
and confirms the selection by entering the name of the element and clicking the 'Add Element' button.
The `document.swdpr_command` object will contain certain properties of the selected element:

* Legacy "indexed" XPath, that looks like `/div[1]/div[1]/table[1]/tbody[1]/tr[1]/td[1]/a[1]/img[1]` - all node attributes are ignored, sibling counts are evaluated.
* Attribute-extended XPath that looks like `//a[@href="/script/answers"]/img[@src="https://codeproject.net/images/question.png" and @alt = 'post an article' ]`
* Firebug-style cssSelector, that look like `ul.nav-links li.nav-links__item div.central.featured.logo-wrapper > img.central.featured-logo` every classes node has all class attributes attached.
* Element text (transalted under the hood into XPath `[contains()]` expression).
* Input for Angular Protractor-specific locators `repeater`, `binding`, `model`, `repeaterRow` etc. (WIP)
* Element ID (when available)
* Element tag name, to help constructing locators for Element text like
```c#
IWebElement element = driver.FindElements(By.TagName("{{TAG_NAME}}")).First(o => String.Compare("{{TEXT}}", o.Text, StringComparison.InvariantCulture) == 0);
```
or
```java
WebElement element = driver.findElement(
By.xpath("//{{TAG_NAME}}[contains(normalize-space(text()), '{{TEXT}}')]")
```
or
```java
WebElement element = driver.findElements(  By.tagName("{{ {{TAG_NAME}} }}"
            .stream().filter(o -> o.getText().contains("{{ TEXT }}") ).findFirst();
```
#### Automation of Locator Shortening
Auto-generated locators often become unnecessarily long, e.g. for the facebook logo one may get:
```css
div#blueBarDOMInspector > div._53jh > div.loggedout_menubar_container >
div.clearfix.loggedout_menubar > div.lfloat._ohe >
h1 > a > i.fb_logo.img.sp_Mlxwn39jCAE.sx_896ebb
```
Currently __SWET__ does not have an algorythm for shortening these locators.
Adding smart locator generators is a work in progress.

### Dependencies Versions

As typical with Selenium, the __SWET__ application only run smoothly  with certain Selenium jar version and it's compatible
version of browser driver and browser itself is used.
The __SWET__ application master branch is being developed with

|                      |              |
|----------------------|--------------|
| SELENIUM_VERSION     | __3.5.1__    |
| FIREFOX_VERSION      | __45.0.1__   |
| CHROME_VERSION       | __61.0.X__   |
| CHROMEDRIVER_VERSION | __2.32__     |

and

|                      |              |
|----------------------|--------------|
| SELENIUM_VERSION     | __2.53.1__   |
| FIREFOX_VERSION      | __45.0.1__   |
| CHROME_VERSION       | __56.0.X__   |
| CHROMEDRIVER_VERSION | __2.29__     |


Some of partially supported version combinations are listed below. Some versions of Selenium ( __3.0.1__ or __3.3.1__ ) have been found unstable.

|                      |              |
|----------------------|--------------|
| SELENIUM_VERSION     | __3.2.0__    |
| FIREFOX_VERSION      | __52.0__ (32 bit)     |
| GECKODRIVER_VERSION  | __0.14__ (32 bit)    |
| CHROME_VERSION       | __57.0.X__   |
| CHROMEDRIVER_VERSION | __2.29__     |

Branches selenium_301 and selenium_3x created until this code is stable in the original project location (`https://github.com/sergueik/selenium_java/tree/master/swd_recorder`).
Stabilizing against the most recent builds of Selenium is a work in progress.

One can download virtually every old build of Firefox from
https://ftp.mozilla.org/pub/firefox/releases, and selected old builds of Chrome from
http://www.slimjetbrowser.com/chrome/, for other browsers the download locations vary.

This is why it may be worthwhile setting up Virtual Box e.g. [selenium-fluxbox](https://github.com/sergueik/selenium-fluxbox) to run the appliation with fixed downlevel browser versions.

### Safari Testing
If you have Mac OSX 10.12.X Sierra / Safari 10.X , then the Apple Safari driver would be installed automatically,
but it does not seems to work with Selenium __2.53__.
For earlier releases, you have to downgrade the Selenium version in the `pom.xml` to __2.48__
then follow the [Running Selenium Tests in Safari Browser](http://toolsqa.com/selenium-webdriver/running-tests-in-safari-browser).

### Code Templates

The code is generated using SWIG templates which look like
```
{#
template: Basic Page Objects (Java)
#}
class TestPage (Page) {
  // {{ ElementText }}
  {% if (ElementSelectedBy == 'ElementCssSelector') -%}
  @FindBy( how = How.CSS, using = "{{ ElementCssSelector }}" )
{% elseif (ElementSelectedBy == 'ElementXPath') -%}
  @FindBy( how = How.XPATH, using = "{{ ElementXPath }}" )
{% elseif (ElementSelectedBy == 'ElementId') -%}
  @FindBy( how = How.ID, using =  "{{ ElementId }}" )
{% endif -%}
{% if (ElementVariable != '') -%}
  private WebElement {{ ElementVariable }};
{% else -%}
  private WebElement element;
{% endif -%}
```
Any language/framework can be supported. The comment
```
{#
template: Basic Page Objects (Java)
#}
```
is reserved for future use, when tester is allowed to provide the path to template during session configuration.

### Configuration, saving and loading

__SWET__ may be saved the element locators in the YAML format, using [snakeyaml](https://bitbucket.org/asomov/snakeyaml). Example:
```yaml
version: '1.0'
created: '2017-02-21'
seleniumVersion: '2.53.1'

# Browser parameters
browser:
  name: firefox
  platform: linux
  version: '45.0.1'

# Browser parameters
browser:
  name: chrome
  platform: windows
  version: '54.0.2840.71'
  driverVersion: '2.24'
  driverPath: 'c:/java/selenium/chromedriver.exe'

# Selenium Browsers
browsers:
  - chrome
  - firefox

# Elements
elements:
  ce094429-d4bd-4eb0-83ab-6d10c563f456:
    ElementCssSelector: div[id = "page-body"] > div.main-container > section.main-content > div.main-content-right > div.row.highlight > section.card.titled > section.project-info > header > h3 > a
    ElementCodeName: 'sourceforge project link'
    Command: AddElement
    Caller: addElement
    ElementPageURL: https://sourceforge.net/
    CommandId: ce094429-d4bd-4eb0-83ab-6d10c563f456
    ElementStepNumber: 1
    ElementSelectedBy: ElementXPath
    ElementText: Staff Choice Outlook CalDav Synchronizer
    ElementId: ''
    ElementVariable: userSelectedVariableName
    ElementXPath: id("page-body")/div[1]/section[1]/div[2]/div[2]/section[1]/section[2]/header[1]/h3[1]/a[@href="/projects/outlookcaldavsynchronizer/?source=frontpage&position=1"]
```
### Custom Configuration
The __SWET__  configirarion file is in `src/main.resources/application.properties`:
```java
username: username
password: password
# no quotes around paths. Trailing whitespace is ok
# use the following syntax for environment variables: ${USERPROFILE}\\desktop\\chromedriver.exe
chromeDriverPath: ${USERPROFILE}\\desktop\\chromedriver.exe
geckoDriverPath: c:/java/selenium/geckodriver.exe
firefoxBrowserPath: c:/Program Files (x86)/Mozilla Firefox/firefox.exe
ieDriverPath: c:/java/selenium/IEDriverServer.exe
```
The `username` and `password` entris are not currently used - these are reserved for Sauce Labs or browserStack.

### Exporing to Keyword-Driven Framework engines

There exist a big number of Keyword-Driven Frameworks with Selenium, e.g. [sergueik/skdf](https://github.com/sergueik/skdf). These allow storing the test flow in e.g. Excel file in a format:
![icon](https://github.com/sergueik/SWET/blob/master/screenshots/table_editor_view.png)
The toolbar buton ![flowchart](https://github.com/sergueik/SWET/blob/master/src/main/resources/images/excel_gen_32.png) does that from SWET.
The actual keyword (like *clickButton*, *getText* , *verifyAttr*  or something else) of the step is not known during the recording, and has to be filled using this form. The rest of the columns come from the saved recording.
It is possible to save the resul as Excel file.

### Work in Progress
* UI improvements adding more form elements
* Testing with Safari and variety of IE / Edge browsers

### Links

#### SWT

  * [main SWT snippets directory](https://www.eclipse.org/swt/snippets/)
  * [SWT examples on javased.com](http://www.javased.com/?api=org.eclipse.swt.widgets.FileDialog)
  * [SWT - Tutorial by Lars Vogel, Simon Scholz](http://www.vogella.com/tutorials/SWT/article.html)
  * [Opal Project (SWT new widgets library) by Laurent Caron](https://github.com/lcaron/opal)
  * [Nebula - Supplemental Widgets for SWT](https://github.com/eclipse/nebula)
  * [danlucraft/jruby-swt-cookbook](https://github.com/danlucraft/jruby-swt-cookbook)
  * [swt Ruby gem](https://github.com/danlucraft/swt)
  * [fab1an/appkit toolkit for swt app design](https://github.com/fab1an/appkit)
  * [SWT dependency repositories](http://stackoverflow.com/questions/5096299/maven-project-swt-3-5-dependency-any-official-public-repo)
  * [SWT jar ANT helper](http://mchr3k.github.io/swtjar/)
  * [Examples](http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/CatalogSWT-JFace-Eclipse.htm)
  * [Examples](https://github.com/ReadyTalk/avian-swt-examples)
  * [swt-bling](https://github.com/ReadyTalk/swt-bling)
  * [Multiplatform SWT](https://github.com/jendap/multiplatform-swt)
  * [SWT Single Jar Packager](https://github.com/mchr3k/swtjar)
  * [SWT custom preference dialog](https://github.com/prasser/swtpreferences) - needs too recent versions of SWT and JFace
  * [SWT multiple choice dialogs](https://github.com/prasser/swtchoices)
  * [Misc JFace/SWT UI elements and utils](https://github.com/Albertus82/JFaceUtils)
  * [SWT System Tray](https://github.com/dorkbox/SystemTray)
  * [System Tray](https://github.com/Vladimir-Novick/System-Tray)
  * [SWT/WMI](https://github.com/ctron/wmisample)
  * [SWT Tools](https://github.com/bp-FLN/SWT-Tools) 
  * [SWTools](https://github.com/Sanglinard26/SWTools)
  * [vogellacompany/swt-custom-widgets](https://github.com/vogellacompany/swt-custom-widgets) eclipse plugin
  * [SWT Browser component based recorder](https://github.com/itspanzi/swt-browser-recorder-spike)
  * [Joptions Pane examples](http://alvinalexander.com/java/java-joptionpane-examples-tutorials-dialogs)
  * [Haixing-Hu/swt-widgets](https://github.com/Haixing-Hu/swt-widgets/wiki/Dialog)
  * [SWING to SWT transformation tool](https://github.com/xgdsmileboy/SWING-SWT-Transformation)
  * [SWT custom components](https://github.com/SWTCustomComponents/SWTCustomComponents)
  * [jGrid - styled SWT grid](https://github.com/GrandmasterTash/jGrid)
  * [Eclipse SWT for Maven Users](https://github.com/maven-eclipse/maven-eclipse.github.io)

#### Eclipse Plugins
  * [Eclipse Plugin documentation](https://github.com/vtrao/Eclipse-Plugin/tree/master/Articles)
  * [java2s](http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/Eclipse-Plugin.htm)
  * [eclipse IDE Plug-in Development] (http://www.vogella.com/tutorials/EclipsePlugin/article.html#firstplugin_migratione4_toolbar)
  * [SeleniumPlus Eclipse plugin](https://github.com/SAFSDEV/SeleniumPlus-Plugin/tree/master/samples)
  * [SelenumPlus Core](https://github.com/SAFSDEV/Core/tree/master/src/org/safs/selenium)

#### Code Generation

  * [Jtwig](https://github.com/jtwig/jtwig)
  * [Thymeleaf](http://www.thymeleaf.org/)
  * [StringTemplate](http://www.stringtemplate.org/)
  * [Protractor page object generator](https://github.com/pageobject-io/pageobject-generator)

#### Existing Selenium Test Generation plugins
  * [IntelliJ Selenium Plugin](http://perfect-test.com/index.php/ru/instruments-rus-menu/15-selenium-plugin-rus), [форум](http://automated-testing.info/t/intellij-selenium-plugin-novyj-plagin-dlya-uproshheniya-sozdaniya-java-webdriver-testov/6514/39)
  * [ui-automation-chrome-extension](https://github.com/TsvetomirSlavov/ui-automation-chrome-extension) - see implementations of `XPathGenerator.js` and `CSSGenerator.js`
  * [wsbaser/Natu.WebSync.Chrome](https://github.com/wsbaser/Natu.WebSync.Chrome) -
  * [watarus-nt/SeleniumGenerator](https://github.com/watarus-nt/SeleniumGenerator)
  * [Silk-WebDriver (not open source)](https://community.microfocus.com/borland/test/silk-webdriver/)
  * [SnapTest](https://www.snaptest.io/) (no source on github -  have to download the `.crx` and find it in `%USERPROFILE%\AppData\Local\Google\Chrome\User Data\Default\Extensions\aeofjocnhdlleichkjbaibdbicpcddhp\0.6.9_0\manifest.json` )

#### Selenium Locator Strategies

  * [Choosing Effective XPaths](http://toolsqa.com/selenium-webdriver/choosing-effective-xpath/)
  * [Use XPath locators efficiently](http://bitbar.com/appium-tip-18-how-to-use-xpath-locators-efficiently/)
  * [XPath-Finder](https://chrome.google.com/webstore/detail/xpath-finder/edglefgecckonkgchckokopkibcpbojb)
  * [XPath-Helper](https://chrome.google.com/webstore/detail/xpath-helper/hgimnogjllphhhkhlmebbmlgjoejdpjl)
  * [CSS Selector Tester](https://chrome.google.com/webstore/detail/css-selector-tester/bbklnaodgoocmcdejoalmbjihhdkbfon)

#### YAML
  * [snakeyaml](https://bitbucket.org/asomov/snakeyaml)

#### Javascript injection
  * [Keymaster](https://github.com/madrobby/keymaster)
  * [SeleniumPlus WebDriver user-extensions.js](https://github.com/SAFSDEV/Core/blob/master/src/org/safs/selenium/user-extensions.js)

#### Misc.

  * [AnarSultanov/InstalledBrowsers](https://github.com/AnarSultanov/InstalledBrowsers)
  * [how to define conditional properties in maven](http://stackoverflow.com/questions/14430122/how-to-define-conditional-properties-in-maven)
  * [eclipse xwt](https://wiki.eclipse.org/XWT_Documentation)
  * [mono/xwt](https://github.com/mono/xwt)
  * [json2](https://github.com/douglascrockford/JSON-js)
  * [geckodriver](http://www.automationtestinghub.com/selenium-3-0-launch-firefox-with-geckodriver/)
  * [jProcesses](https://github.com/profesorfalken/jProcesses)
  * [jedi-tester/Ext2POI](https://github.com/jedi-tester/Ext2POI)

### Note

[Swet](http://www.urbandictionary.com/define.php?term=swet&defid=6820405) - *a word that describes something that's hot. Or something that would typically take a lot of skill and practice to do, therefore causing the person to sweat*.

####  Source Code History

The code was originally located inside a much larger repository: `https://github.com/sergueik/selenium_java/tree/master/swd_recorder`.
On April 11 2017 the master branch HEAD was copied into separate project, to track past histories and branches please review the original project location.

### License
This project is licensed under the terms of the MIT license.

### Author
[Serguei Kouzmine](kouzmine_serguei@yahoo.com)
