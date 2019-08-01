### Info

![icon](https://github.com/sergueik/SWET/blob/master/src/main/resources/images/document_wrench_color.png)

__Selenium WebDriver Elementor Toolkit__  ( __SWET__  = light , in Russian) is a OS-independent successor to the
[Selenium WebDriver Page Recorder](https://github.com/dzharii/swd-recorder) (__SWD__) by Dmytro Zharii and author.
__SWET__ is using [Eclipse Standard Widget Toolkit](https://www.eclipse.org/swt/) with third party [Opal](https://github.com/lcaron/opal) widget library'
instead of Microsoft .Net Windows Forms for user interface and [Jtwig](http://jtwig.org/documentation/reference) template engine
instead of [ASP.Net Razor](https://en.wikipedia.org/wiki/ASP.NET_Razor) for code generation.
That is just one of the available template exngines - note, __jtwig__ supports the original [PHP Twig](http://twig.sensiolabs.org/doc/2.x/)
syntax as well.

__SWET__ also allows generating the __keyword driven framework__ flow and saving it into an Excel spreadsheet.
This is a work in progress, since each keyword driven framework has its own list of *keywords* , currenlty __SWET__ supports the
[SKDF](https://github.com/sergueik/SKDF) framework developed by the author.


__SWET__ was tested to work on 32 or 64 bit Windows, Mac or Linux platforms.
__SWET__ is currently beta quality: one can record, load and save sessions, update individual Page Element information,
convert session to a code fragment in Java or C# language, or an Excel file with a Keyword Driven Framework workflow.
The __SWET__ project was originally written to offer similar functionality as __SWD__, but significantly less [MVC](https://www.codeproject.com/Articles/383153/The-Model-View-Controller-MVC-Pattern-with-Csharp)-heavyy and also
breaking its dependency on Windows Forms libraries, after an attempt to replace Windows Forms with [XWT](https://github.com/mono/xwt) was made failed with conclysion that XWT look and feel of the time,
did not seem equivalent. Eventually the full functionality of __SWD__ is to be achieved, also __SWET__ might become an [Eclipse plugin](http://www.vogella.com/tutorials/EclipsePlugin/article.html).

The __SWET__ application is being developed in Ecipse with [SWT Designer/Window Builder](http://www.vogella.com/tutorials/EclipseWindowBuilder/article.html) plugin,
on Ubuntu 16.04 and Windows. The [javaFx](http://www.java2s.com/Tutorials/Java/JavaFX/) port os __SWET__ is in the works.

For Mac / Safari testing, the [Sierra Final 10.12](https://techsviewer.com/install-macos-sierra-virtualbox-windows/) Virtual Box by TechReviews is being used.
Naturally, Apple only licenses OS X for use on their hardware, therefore the fact the author has been using able to boot Sierra and test the code to successfully compile and run
does not imply he officialy asks anyone do the same, it simply proves the code being robust enough and very likely will run on a real Mac out of gitgub.
Currently, recording page elements in __SWET__ with Safari browser is somewhat flaky but possible.

The Virtualbox images are setup for Selenium 3.x testing (work in progress).

![OSX Example](https://github.com/sergueik/SWET/blob/master/screenshots/capture1.png)

![Ubuntu Example](https://github.com/sergueik/SWET/blob/master/screenshots/capture2.png)

![Windows Example](https://github.com/sergueik/SWET/blob/master/screenshots/capture3.png)

### Usage

In order to use __SWET__ one will need to compile the application jar from the source - it is not difficult.
Continue reading for info on how to get the dev environment setup.

### Prerequisites
The project can be compiled and run from Eclipse or standalone, through the launcher script. The single executable jar is not being packages by default due to its size - anyone is free to
try on one's own risk.


To build the project outside of Eclipse, JDK 1.8 or later and Maven need to be installed and added to the `PATH`.
There launcher  scripts is  explained below.

As the time goes on, the following reusable parts of the __SWET__ project became stanalone ones and have been eitherpublished to maven central or need to be cloned and installed locally using maven:

* [jProtractor](https://github.com/sergueik/jProtractor)
* [SKDF](https://github.com/sergueik/SKDF)
* [jUnitParams dataproviders](https://github.com/sergueik/junit-dataproviders)

To skip smoke testing of those projects use the command
```cmd
mvn -Dmaven.test.skip=true install
```
to have them available in `~/.m2/repositories`. Eventually all of these prjects would go to maven central.

In the checked-in sample runner scripts, the JDK and Maven is expected to be installed under `c:\java\` on Windows,
or into the path
`/Library/Java/JavaVirtualMachines/jdk$JAVA_VERSION.jdk/Contents/Home`(which is the default location) on the Mac,
and made available on Linux with [`alternatives`](https://www.linuxquestions.org/linux/answers/Applications_GUI_Multimedia/LINUX_ALTERNATIVES_HOWTO).

All project dependencies are pulled by Maven.

#### Updating the platform-specific information in the `pom.xml`

There are profiles in the project `pom.xml` which make selection of the platform-specific `swt.jar` dependency automatic:

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
The correct profile is likely be selected automatically. If this does not work for some reason, one will need to copy the relevant
`artifactId` property definition into the
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

Due to some problem with JVM loader, all these platform-dependent jars cannot be included simultaneously.
One alternative is to package the spring-boot style uber-jar file as explained in
[Multiplatform SWT](https://github.com/jendap/multiplatform-swt) project.
Unfortulately it is not very practical: the resulting bare-bones `multiplatform-swt-loader.jar` file is almost 10 Mb
and with all Selenium JSON POI and TWIG dependencies the size of __SWET__ jar exceeds 30 Mb.
The conversion to javaFx is currently underway.
Therefore,  we recommend to rely on maven profile facility to pick the required dependency and
to modify the `pom.xml` and use runner scripts to compile it as explained below.

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
On Unix or a Mac, launcher is a bash script
```bash
./run.sh
```

### Debugging and adding Features

The __SWET__ application is a Gimp style multi dialog desktop app. Practically every class (SWT or console) in the project directory can be started standalone with the help of the 
launcher script(s) `run.cmd`, `run.ps1` in Windows OS and `run.sh` in OSX or Linux os like e.g.:

```cmd
run.cmd ComplexFormEx
```
compiles the app and launches the specific class' `main` method which most of the classes already provide:
```java
@SuppressWarnings("unused")
public static void main(String[] arg) {
  ComplexFormEx o = new ComplexFormEx(null, null);
  o.render();
}
```
![Running and debugging Form Example](https://github.com/sergueik/SWET/blob/master/screenshots/capture_run_complexformex.png)

##
The script has ability to also download the dependency jar(s), if there are ones not hosted on Maven Central repository ( *this is no longer necessary* ),
then to compile and package the project using maven
and run the application main class from the `target` directory jar and libraries.

The script configuration needs to be updated with the actual paths to Java and Maven:
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
java.exe -cp target\swet-0.0.5-SNAPSHOT.jar;target\lib\* com.github.sergueik.swet.SimpleToolBarEx
```
or

```bash
java.exe -cp target/swet-0.0.5-SNAPSHOT.jar:target/lib/* com.github.sergueik.swet.SimpleToolBarEx
```
- without changing to the source, there is little reason to recompile it every time. Adding the `META-INF/MANIFEST.MF`
is a work in progress.

Paths to the browsers and browser drivers are read from the `src/main.resources/application.properties` file
```java
# username / password not currently used - reserved for Sauce Labs or browserStack
username: username
password: password
# no quotes should be put around paths. Trailing whitespace is ok
# use the following syntax for environment variables: ${USERPROFILE}\\desktop\\chromedriver.exe
chromeDriverPath: ${USERPROFILE}\\desktop\\chromedriver.exe
# chromeDriverPath: ${HOME}/Downloads/chromedriver
geckoDriverPath: c:/java/selenium/geckodriver.exe
firefoxBrowserPath: C:/Program Files (x86)/Mozilla Firefox/firefox.exe
# firefoxBrowserPath: /Applications/Firefox.app/Contents/MacOS/firefox
geckoDriverPath: c:/java/selenium/geckodriver.exe
# geckoDriverPath: ${HOME}/Downloads/geckodriver
ieDriverPath: c:/java/selenium/IEDriverServer.exe
edgeDriverPath: C:/Program Files (x86)/Microsoft Web Driver/MicrosoftWebDriver.exe
```
Eventually other common formats: YAML, JSON, POI or Java properties file - will be supported.


### Recording the page elements with SWET.

#### Toolbar Buttons

![launch](https://github.com/sergueik/SWET/blob/master/src/main/resources/images/launch_32.png)
launches the browser

![launch](https://github.com/sergueik/SWET/blob/master/src/main/resources/images/find_32.png)
injects the [SWD Element Searcher script](https://github.com/sergueik/SWET/blob/master/src/main/resources/ElementSearch.js)
into the page, then starts polling the page waiting for user to select some element via ![SWD Mouse Action](https://github.com/sergueik/SWET/blob/master/screenshots/ctrl_right_button_bw_32.png) CTRL + right click and to fill and submit the form:
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

The flow export toolbar buton ![flowchart](https://github.com/sergueik/SWET/blob/master/src/main/resources/images/excel_gen_32.png)
converts SWET recording into a Keyword Driven Framework flow that can be later saved in the Excel file.
There exist a big number of Keyword-Driven Frameworks with Selenium, e.g. [sergueik/skdf](https://github.com/sergueik/skdf). These allow storing the test flow in e.g. Excel file in a format:
![icon](https://github.com/sergueik/SWET/blob/master/screenshots/table_editor_view.png)
The actual keyword (like *clickButton*, *getText* , *verifyAttr*  or something else) of the step is not known during the recording, and has to be filled using this form.
The rest of the columns gets read from the saved recording.
It is possible to save the resul as Excel file:
![icon](https://github.com/sergueik/SWET/blob/master/screenshots/exported_flow.png)

The preferences button
![preferences](https://github.com/sergueik/SWET/blob/master/src/main/resources/images/gear_36.png)
opens the configuration dialog
![config](https://github.com/sergueik/SWET/blob/master/screenshots/config.png)
Currently the browser and template selection are configurable, one also can set the base URL.

Currently project is hardcoded to start using Chrome browser on Windows, Safari on Mac and Firefox on the rest of platforms.
Alternative  browser can be selected during the session, but this preference is not saved.
Saving / loading the session configuration from the YAML configuration file is a work in progress.

#### Operation
Both __SWD__ and __SWET__ inject certain Javascript code `ElementSearch.js` into the page, that the user may interact with with the ![SWD Mouse Action](https://github.com/sergueik/SWET/blob/master/screenshots/ctrl_right_button_bw_32.png) CTRL key + mouse right-click.
After injecting the script the IDE waits polling for the speficic
`document.swdpr_command` object to be created on that page. This object is created  by the `ElementSearch.js`
after user selects the specific element on the page, fills the form defining the *name* of that element
and confirms the selection by clicking the 'Add Element' button.
The `document.swdpr_command` object received by SWD application will contain the following properties of the selected element:

* Legacy "indexed" xpath, that looks like `/div[1]/div[1]/table[1]/tbody[1]/tr[1]/td[1]/a[1]/img[1]` - all node attributes are ignored, sibling counts are evaluated.
* Attribute-extended xpath that looks like `//a[@href="/script/answers"]/img[@src="https://codeproject.net/images/question.png" and @alt = 'post an article' ]`
* Firebug-style css selector, that look like `ul.nav-links li.nav-links__item div.central.featured.logo-wrapper > img.central.featured-logo` every classes node has all class attributes attached.
* Element text (translated under the hood into XPath `[contains()]` expression).
* Input for Angular Protractor-specific locators `repeater`, `binding`, `model`, `repeaterRow` etc. (WIP)
* Element ID (when available)
* Element tag name, which is often helpful together with element's text content in constructing element  locators like
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
Currently __SWET__ has no algorythm for shortening such locators.
Adding a smart locator trimmers is a work in progress.

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
If you have Mac 10.12.X Sierra / Safari 10.X , then the Apple Safari driver would be installed automatically,
but it does not seems to work with Selenium __2.53__.
For earlier OS X releases, you have to downgrade the Selenium version in the `pom.xml` to __2.48__
then follow the [Running Selenium Tests in Safari Browser](http://toolsqa.com/selenium-webdriver/running-tests-in-safari-browser).

### Code Templates

The code is generated using [jtwig](http://jtwig.org/) templates which look like
```java
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
Any language syntax can be supported. The comment
```java
{#
template: Name of the Template
#}
```
is reserved for the name of the template, when creator is allowed to provide the path to template during session configuration -  every teplate file `template.twig` will be found and added to the formatter selection
(one may need to reopen the configuration dialog to actually see the new templates). For the example above it will be shown as

![Ubuntu Example](https://github.com/sergueik/SWET/blob/master/screenshots/config_browse.png)
![Ubuntu Example](https://github.com/sergueik/SWET/blob/master/screenshots/config_browse_result.png)


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
The `username` and `password` entris are not currently used - these are reserved for possible users with accounts on Sauce Labs or browserStack.

License

__SWET__is licensed under Apache License, Version 2.0

### Links

#### SWT

  * [main SWT snippets directory](https://www.eclipse.org/swt/snippets)
  * [SWT examples on javased.com](http://www.javased.com/?api=org.eclipse.swt.widgets.FileDialog)
  * [SWT - Tutorial by Lars Vogel, Simon Scholz](http://www.vogella.com/tutorials/SWT/article.html)
  * [Opal Project (SWT new widgets library) by Laurent Caron](https://github.com/lcaron/opal)
  * [Nebula - Supplemental Widgets for SWT](https://github.com/eclipse/nebula)
  * [danlucraft/jruby-swt-cookbook](https://github.com/danlucraft/jruby-swt-cookbook)
  * Misc. [SWT widgets](https://github.com/greipadmin/greip), in particular, the `StyledLabel`
  * [swt Ruby gem](https://github.com/danlucraft/swt)
  * [fab1an/appkit toolkit for swt app design](https://github.com/fab1an/appkit)
  * [SWT dependency repositories](http://stackoverflow.com/questions/5096299/maven-project-swt-3-5-dependency-any-official-public-repo)
  * [SWT jar ANT helper](http://mchr3k.github.io/swtjar)
  * [Examples](http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/CatalogSWT-JFace-Eclipse.htm)
  * [Examples](https://github.com/ReadyTalk/avian-swt-examples)
  * [swt-bling](https://github.com/ReadyTalk/swt-bling)
  * [int32at/sweaty](https://github.com/int32at/sweaty)
  * [Multiplatform SWT](https://github.com/jendap/multiplatform-swt)
  * [SWT Single Jar Packager](https://github.com/mchr3k/swtjar)
  * [SWT custom preference dialog](https://github.com/prasser/swtpreferences) - needs too recent versions of SWT and JFace
  * [SWT multiple choice dialogs](https://github.com/prasser/swtchoices)
  * [Misc JFace/SWT UI elements and utils](https://github.com/Albertus82/JFaceUtils)
  * [SWT System Tray](https://github.com/dorkbox/SystemTray)
  * [Chromium widget for SWT](https://github.com/maketechnology/chromium.swt) 
  * [Console](https://github.com/dorkbox/Console)
  * [Notify](https://github.com/dorkbox/Notify)
  * [System Tray](https://github.com/Vladimir-Novick/System-Tray)
  * [SWT/WMI](https://github.com/ctron/wmisample)
  * [SWT Tools](https://github.com/bp-FLN/SWT-Tools)
  * [SWTools](https://github.com/Sanglinard26/SWTools)
  * [SWT Button conrol with a drop down menu](https://github.com/SubashJanarthanan/SWT_Button_DropDown)
  * [vogellacompany/swt-custom-widgets](https://github.com/vogellacompany/swt-custom-widgets) eclipse plugin
  * [SWT Browser component based recorder](https://github.com/itspanzi/swt-browser-recorder-spike)
  * [Joptions Pane examples](http://alvinalexander.com/java/java-joptionpane-examples-tutorials-dialogs)
  * [Haixing-Hu/swt-widgets](https://github.com/Haixing-Hu/swt-widgets/wiki/Dialog)
  * [SWING to SWT transformation tool](https://github.com/xgdsmileboy/SWING-SWT-Transformation)
  * [SWT UI described in JSON](https://github.com/milanaleksic/baobab)
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

#### Existing Selenium Test Generation recorders and plugins

  * [Katalon Studio overview (in Russian)](http://www.software-testing.ru/library/testing/testing-automation/2551-katalon-studio), official memo covering [Katalon Studio GUI Linux version](https://docs.katalon.com/katalon-studio/docs/katalon-studio-gui-beta-for-linux.html)
  * [Muse Test Framework](https://github.com/ChrisLMerrill/muse), [Muse examples](https://github.com/ChrisLMerrill/muse-examples),  [Muse IDE](https://github.com/ChrisLMerrill/muse-pageobjects)
  * [Bromiomi browser based framework for running automated integration and end-to-end tests](https://github.com/hristo-vrigazov/bromium)
  * [alibaba/uirecorder](https://github.com/alibaba/uirecorder)
  * [selenium2/webdriver test script recorder](https://github.com/fudax/selenium_recorder), uses Swing
  * [top 10 test automation instruments](http://automated-testing.info/t/top-10-instrumentov-avtomatizaczii-testirovaniya-2018/17404)(in russian)
  * [links in the Selenium IDE retirement memo](https://blog.testproject.io/2017/09/03/farewell-selenium-ide/)
  * [CognizantQAHub/Cognizant-Intelligent-Test-Scripter](https://github.com/CognizantQAHub/Cognizant-Intelligent-Test-Scripter)
  * [CITS IE Toolbar (c#)](https://github.com/CognizantQAHub/Cognizant-Intelligent-Test-Scripter-IE-Toolbar)
  * [CITS-Chrome-Extension](https://github.com/CognizantQAHub/Cognizant-Intelligent-Test-Scripter-Chrome-Extension)
  * [CITS-Firefox-Addon](https://github.com/CognizantQAHub/Cognizant-Intelligent-Test-Scripter-Firefox-Addon)
  * [IntelliJ Selenium Plugin](http://perfect-test.com/index.php/ru/instruments-rus-menu/15-selenium-plugin-rus), [форум](http://automated-testing.info/t/intellij-selenium-plugin-novyj-plagin-dlya-uproshheniya-sozdaniya-java-webdriver-testov/6514/39)
  * [ ui-automation-chrome-extension](https://github.com/TsvetomirSlavov/ui-automation-chrome-extension) - see implementations of `XPathGenerator.js` and `CSSGenerator.js`
  * [wsbaser/Natu.WebSync.Chrome](https://github.com/wsbaser/Natu.WebSync.Chrome)
  * [watarus-nt/SeleniumGenerator](https://github.com/watarus-nt/SeleniumGenerator)
  * [Silk-WebDriver (not open source)](https://community.microfocus.com/borland/test/silk-webdriver/)
  * [SnapTest](https://www.snaptest.io/) (no source on github -  have to download the `.crx` and find it in `%USERPROFILE%\AppData\Local\Google\Chrome\User Data\Default\Extensions\aeofjocnhdlleichkjbaibdbicpcddhp\0.6.9_0\manifest.json` )
  * [ChroPath Chrome extension](https://chrome.google.com/webstore/detail/chropath/ljngjbnaijcbncmcnjfhigebomdlkcjo?hl=en)
  * [ChroPath for Firefox (claims to be compatible only with FF 48+)](https://addons.mozilla.org/en-US/firefox/addon/chropath-for-firefox/)
  * [Catchpoint Script Recorder (alternative download of version 1.9.5 (ondjkacanajgdlnhcnpiapajjpgilplo), no longer in App Store)](https://www.crx4chrome.com/crx/49424/)
  * [FireBreath cross-platform browser plugin framework](https://github.com/firebreath/FireBreath) see also [](https://www.firebreath.org/)
  * [UI Recorder (nodejs)](https://github.com/alibaba/uirecorder)
  * [AutonomiQ - Test Data  / Test Script Generation - commercial](https://autonomiq.io/) 

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
  * [free java license-management projects](http://freshmeat.sourceforge.net/tags/license-management)
  * [Hamcrest tutorial](http://www.vogella.com/tutorials/Hamcrest/article.html)
  * [Autoit documentation](https://www.autoitscript.com/autoit3/docs/introduction.htm)
  * [Autoit JNA wrapper](https://github.com/midorlo/JNAutoIt), also forked. 
  * [Autoit COM jva bridge](https://github.com/accessrichard/autoitx4java)
  * [Mock server](https://github.com/jamesdbloom/mockserver)
  
### Note

[Swet](http://www.urbandictionary.com/define.php?term=swet&defid=6820405) - according to 
urbanictionary, describes *something that's hot. Or something that would typically take a lot of skill and practice to do, therefore causing the person to sweat*.

####  Source Code History

The code was originally located inside a much larger repository: `https://github.com/sergueik/selenium_java/tree/master/swd_recorder`.
On April 11 2017 the master branch HEAD was copied into separate project, to track past histories and branches please review the original project location.

### License
This project is licensed under the terms of the MIT license.

### Author
[Serguei Kouzmine](kouzmine_serguei@yahoo.com)
