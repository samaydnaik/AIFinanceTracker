package com.aiFinanceTracker.track.dto;

import java.util.List;

public class SpendingAnalysisResult {

    private String summary;
    private List<String> topIssues;
    private List<String> recommendations;
    private Integer score; // 0–10, nullable
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	public List<String> getRecommendations() {
		return recommendations;
	}
	public void setRecommendations(List<String> recommendations) {
		this.recommendations = recommendations;
	}
	public List<String> getTopIssues() {
		return topIssues;
	}
	public void setTopIssues(List<String> topIssues) {
		this.topIssues = topIssues;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}

    // getters/setters
}