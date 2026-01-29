package com.monza.app.api.dto;

public class VoteResponse {
    private int upvoteCount;
    private int downvoteCount;
    private Integer currentUserVote; // 1, -1 or null

    public VoteResponse() {}

    public VoteResponse(int upvoteCount, int downvoteCount, Integer currentUserVote) {
        this.upvoteCount = upvoteCount;
        this.downvoteCount = downvoteCount;
        this.currentUserVote = currentUserVote;
    }

    public int getUpvoteCount() { return upvoteCount; }
    public int getDownvoteCount() { return downvoteCount; }
    public Integer getCurrentUserVote() { return currentUserVote; }

    public void setUpvoteCount(int upvoteCount) { this.upvoteCount = upvoteCount; }
    public void setDownvoteCount(int downvoteCount) { this.downvoteCount = downvoteCount; }
    public void setCurrentUserVote(Integer currentUserVote) { this.currentUserVote = currentUserVote; }
}
