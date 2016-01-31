package org.kivio.roller.plugins.github;

public class Profile {
	// your real name on GitHub like Bjoern Berg
	private String realname;
	// your location like Germany
	private String location;
	// your username
	private String username;
	// your mail address
	private String email;
	// your number of followers
	private int followersCount;
	// URL of avatar image
	private String avatarUrl;

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(int followerCount) {
		this.followersCount = followerCount;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
}
