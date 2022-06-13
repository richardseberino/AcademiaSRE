package com.gm4c.tef.repository;

public class DependenciesHealthIndicatorRepository {
        
	private String name;
    private boolean enabled;
    private int level;
    private long threshold;
    private String url;
    private String topicreq;
	
    public String getName() {
    	return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public long getThreshold() {
		return threshold;
	}
	public void setThreshold(long threshold) {
		this.threshold = threshold;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTopicreq() {
		return topicreq;
	}
	public void setTopicreq(String topicreq) {
		this.topicreq = topicreq;
	}

}