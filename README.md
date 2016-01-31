*This plugin is beta - use with caution*

#Apache Roller GitHub plugin
This Apache Roller PageModel plugin helps you integrating information from [GitHub](http://github.org) into your blog. Currently you can add a GitHub repository to each blog. See section Usage for more details.

##How to build
Before you can build this plugin you have to retrieve and build the Roller sources. Do the following:

	$ git clone https://github.com/apache/roller.git
	$ cd roller
	$ mvn clean install
	
Finally get and build this plugin:
	
	$ git clone https://github.com/rollinhand/roller-github-plugin.git
	$ cd roller-github-plugin
	$ mvn clean package

Build assembles all needed dependencies into the plugin.

##Usage
Copy the GitHub plugin jar file into the WEB-INF/lib directory of your Roller installation.

Include the GitHub plugin in the list of plugins in your roller-custom.properties file:

	rendering.pageModels=\
	...
	org.apache.roller.weblogger.ui.rendering.model.MenuModel,\
	org.kivio.roller.plugins.github.GitHubModel
	
	rendering.previewModels=\
	...
	org.apache.roller.weblogger.ui.rendering.model.MenuModel,\
	org.kivio.roller.plugins.github.GitHubModel

To use this plugin you have to edit your roller-custom.properties and add your github credentials for each blog:

	BLOGNAME.github.login=your.username@github.org
	BLOGNAME.github.password=Xxx
	
_BLOGNAME_ is a placeholder for the shortname of your weblog that is identified by the URL.

Restart roller and you can access some GitHub information with the following commands:
	
	$repositories
	$activities
	$profile
	
The variables repositories and activities hold list which can be iterated with #foreach.	

##Feedback
I am not able to test this plugin in a mulit-blog environment. Please let me know of any improvements you like to see for this plugin and send an email to [rollin.hand@gmx.de](mailto:rollin.hand@gmx.de).


	
