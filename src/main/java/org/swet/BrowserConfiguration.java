package org.swet;

import static java.lang.String.format;

/**
 * Browser Configuration Serializer class for Selenium Webdriver Elementor Tool (SWET)
 * @author Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public final class BrowserConfiguration {
	public String name;
	public String platform;
	public String driverPath;
	public String driverVersion;
	public String version;

	public String getName() {
		return name;
	}

	public void setName(String data) {
		this.name = data;
	}

	public String getDriverVersion() {
		return driverVersion;
	}

	public void setDriverVersion(String data) {
		this.driverVersion = data;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String data) {
		this.version = data;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String data) {
		this.platform = data;
	}

	public String getDriverPath() {
		return driverPath;
	}

	public void setDriverPath(String data) {
		this.driverPath = data;
	}

	@Override
	public String toString() {
		return String.format("Browser '%s' version '%s'%s on '%s'%s", getName(),
				getVersion(),
				(getDriverVersion() == null) ? ""
						: String.format(", driver version '%s'", getDriverVersion()),
				getPlatform(),
				(getDriverPath() == null) ? ""
						: String.format(", with path to driver set to '%s'",
								getDriverPath()));
	}
}