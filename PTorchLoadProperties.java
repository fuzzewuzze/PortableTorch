package com.gmail.fuzzelogicsoftware.PortableTorch;

public class PTorchLoadProperties {

	public static void loadMain(){
	     String propertiesFile = PTorch.maindirectory + "config.yml";
	     PTorchProperties properties = new PTorchProperties(propertiesFile);
	     properties.load();
	    
	     PTorch.serverTicks = properties.getInteger("lightTime", 1000);
	    
	    }
	}
