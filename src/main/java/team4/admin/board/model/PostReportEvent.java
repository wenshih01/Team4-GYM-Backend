package team4.admin.board.model;

public class PostReportEvent {
	private final Integer recipientId;
	private final Integer postId;
	private final boolean isRemoved;

	public PostReportEvent(Integer recipientId, Integer postId, boolean isRemoved) {
		this.recipientId = recipientId;
		this.postId = postId;
		this.isRemoved = isRemoved;
	}

	public Integer getRecipientId() {
		return recipientId;
	}

	public Integer getPostId() {
		return postId;
	}

	public boolean isRemoved() {
		return isRemoved;
	}
	
	
}
