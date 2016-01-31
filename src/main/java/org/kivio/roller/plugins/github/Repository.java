package org.kivio.roller.plugins.github;

import java.net.URL;

public class Repository {
	// name of the repository
	private String name;
	// repositorie's description
	private String description;
	// number of forks
	private int forks;
	// URL to repository
	private URL url;
	// watchers (stars)
	private int watchers;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getForks() {
		return forks;
	}
	public void setForks(int forks) {
		this.forks = forks;
	}
	public URL getUrl() {
		return url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}
	public int getWatchers() {
		return watchers;
	}
	public void setWatchers(int watchers) {
		this.watchers = watchers;
	}
}
