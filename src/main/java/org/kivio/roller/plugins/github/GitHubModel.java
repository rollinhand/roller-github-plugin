package org.kivio.roller.plugins.github;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.roller.weblogger.WebloggerException;
import org.apache.roller.weblogger.config.WebloggerConfig;
import org.apache.roller.weblogger.pojos.Weblog;
import org.apache.roller.weblogger.ui.rendering.model.Model;
import org.apache.roller.weblogger.ui.rendering.util.WeblogPageRequest;
import org.apache.roller.weblogger.ui.rendering.util.WeblogRequest;
import org.apache.roller.weblogger.ui.rendering.velocity.RollerVelocity;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.kohsuke.github.GHEventInfo;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;
import org.kohsuke.github.PagedIterator;

public class GitHubModel implements Model {
	private static final long FIVE_MINUTES = 5 * 60 * 1000;
	
	private static final int PAGE_SIZE = 10;
	
	private static final String PROPERTY_LOGIN = ".github.login";
	
	private static final String PROPERTY_PASSWORD = ".github.password";
	
	private static final Log log = LogFactory.getLog(GitHubModel.class);
	
	private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private WeblogPageRequest pageRequest = null;
	
	private Weblog weblog = null;
	
	private List<PublicActivity> activities = null;
	
	private List<Repository> repositories = null;
	
	private Profile profile = null;
	
	private GitHub github = null;
	
	private Date lastRetrieval = null;
	
	private VelocityEngine engine = null;
	
	private String login = null;
	
	private String password = null;
	
	public GitHubModel() {
		log.debug("GitHubModel created.");
	}

	public String getModelName() {
		return "github";
	}

	public void init(Map initData) throws WebloggerException {
		// we expect the init data to contain a weblogRequest object
        WeblogRequest weblogRequest = (WeblogRequest) initData.get("parsedRequest");
        if(weblogRequest == null) {
            throw new WebloggerException("expected weblogRequest from init data");
        }
        
        // PageModel only works on page requests, so cast weblogRequest
        // into a WeblogPageRequest and if it fails then throw exception
        if(weblogRequest instanceof WeblogPageRequest) {
            this.pageRequest = (WeblogPageRequest) weblogRequest;
        } else {
            throw new WebloggerException("weblogRequest is not a WeblogPageRequest."+
                    "  PageModel only supports page requests.");
        }
        
        log.debug("Detect Weblog...: " + weblogRequest);
        
        weblog = pageRequest.getWeblog();
        String weblogName = weblog.getName().toLowerCase();
        
        log.info("Weblog-Name: " + weblogName);
		
		try {
			log.debug("GitHubModel init called");
			
			// Extend engine only once
			if (engine == null) {
				engine = RollerVelocity.getEngine();
				extendVelocityEngine(engine);
			}
			
			try {
				// Load settings from global - throws exception if key not found
				this.login 		= WebloggerConfig.getProperty(weblogName + PROPERTY_LOGIN);
				this.password 	= WebloggerConfig.getProperty(weblogName + PROPERTY_PASSWORD); 
			} catch (Exception e) {
				log.warn("Key not found.", e);
			}
			
			// Settings not available, look for repositories.properties in classpath
			if (StringUtils.isEmpty(login)) {
				Properties ghProps = new Properties();
				ghProps.load(getClass().getResourceAsStream("/repositories.properties"));
				this.login 		= ghProps.getProperty(weblogName + PROPERTY_LOGIN);
				this.password 	= ghProps.getProperty(weblogName + PROPERTY_PASSWORD);
			}
			
			if (StringUtils.isNotEmpty(this.login)) {
				github = GitHub.connectUsingPassword(this.login, this.password);
			} else {
				throw new WebloggerException("plugin GitHub not initialized");
			}
		} catch (IOException e) {
			log.error("ERROR contacting GitHub", e);
		} catch (Exception e) {
			log.error("ERROR initializing GitHubModel", e);
		}
	}
	
	protected void extendVelocityEngine(VelocityEngine engine) {
		log.info("adding additional resource loader: github");
		engine.addProperty(VelocityEngine.RESOURCE_LOADER, "github");
		engine.addProperty("github.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.addProperty("github.resource.loader.cache", false);
		engine.addProperty("github.resource.loader.path", ".");
		engine.addProperty(Velocity.VM_LIBRARY, "github.vm");
		log.info("restarting engine");
		engine.init();
	}
	
	protected void loadRepositories() {
		try {
			Map<String, GHRepository> repos = github.getMyself().getAllRepositories();
			log.debug("retrieved repositories: " + repos.size());
			
			// convert map to list
			log.debug("convert map to list");
			repositories = new LinkedList<Repository>();
			for ( String name : repos.keySet() ) {
				GHRepository gh = repos.get(name);
				Repository r = new Repository();
				log.debug("adding repository: " + name);
				r.setName(name);
				r.setDescription(gh.getDescription());
				r.setForks(gh.getForks());
				r.setWatchers(gh.getWatchers());
				r.setUrl(gh.getHtmlUrl());
				repositories.add(r);
			}
		} catch (Exception e) {
			log.error("ERROR loading repositories", e);
		}
	}
	
	public void loadProfile() {
		log.debug("loading profile");
		try {
			GHMyself myself = github.getMyself();
			this.profile = new Profile();
			
			profile.setRealname(myself.getName());
			profile.setUsername(myself.getLogin());
			profile.setLocation(myself.getLocation());
			profile.setEmail(myself.getEmail());
			profile.setAvatarUrl(myself.getAvatarUrl());
			profile.setFollowersCount(myself.getFollowersCount());
		} catch (Exception e) {
			log.error("ERROR reading profile", e);
		}
	}
	
	public void loadActivities() {
		log.debug("loading public activities");
		try {
			PagedIterable<GHEventInfo> events = github.getMyself().listEvents();
			PagedIterator<GHEventInfo> it = events.withPageSize(PAGE_SIZE).iterator();
			List<GHEventInfo> latestEvents = it.nextPage();
			
			activities = new LinkedList<PublicActivity>();
			
			for ( GHEventInfo event : latestEvents ) {
				PublicActivity activity = new PublicActivity();
				activity.setUsername( event.getActor().getLogin() );
				activity.setRepository( event.getRepository().getFullName() );
				activity.setCreatedAt( event.getCreatedAt() );
				activity.setEvent( event.getType().name() );
				activities.add(activity);
			}
		} catch (Exception e) {
			log.error("ERROR loading activities");
		}
	}
	
	/***
	 * Check how long last poll of GitHub is ago. If its less than five minutes
	 * ago we do not check again. There are not so much changes.
	 * 
	 * @return true if new poll has to be done.
	 */
	public boolean updateNeeded() {
		Date now = new Date();
		boolean updateNeeded = false;
		
		if (lastRetrieval == null) {
			updateNeeded = true;
			lastRetrieval = now;
		} else {
			
			updateNeeded = lastRetrieval.getTime() < ( now.getTime() - FIVE_MINUTES );
		}
		
		log.debug("Update needed: " + updateNeeded + ", Now: " + df.format(now) + " / Last: " + df.format(lastRetrieval));
		
		return updateNeeded;
	}

	public List<PublicActivity> getActivities() {
		if (this.activities == null || updateNeeded() ) {
			loadActivities();
		}
		return activities;
	}

	public List<Repository> getRepositories() {
		if (this.repositories == null || updateNeeded() ) {
			loadRepositories();
		}
		return repositories;
	}

	public Profile getProfile() {
		if (this.profile == null || updateNeeded() ) {
			loadProfile();
		}
		return profile;
	}
}
