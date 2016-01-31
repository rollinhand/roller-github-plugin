package org.kivio.roller.plugins.github;

import java.util.Date;

public class PublicActivity {
	// Actor who did the activity
	private String username;
	// Name of the repository
	private String repository;
	
	private Date createdAt;
	
	private String event;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}
	
	
}
