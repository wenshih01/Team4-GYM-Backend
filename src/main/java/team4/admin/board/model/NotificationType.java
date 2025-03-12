package team4.admin.board.model;

public enum NotificationType {
	 LIKE_POST("like_post", "對你的貼文按讚了"),
	    COMMENT_POST("comment_post", "在你的貼文發表了留言"),
	    LIKE_COMMENT("like_comment", "對你的留言按讚了"),
	POST_REPORTED("post_reported", "你的貼文因違反社群規範已被移除");

	    private final String type;
	    private final String messageTemplate;

	    NotificationType(String type, String messageTemplate) {
	        this.type = type;
	        this.messageTemplate = messageTemplate;
	    }

	    public String getType() {
	        return type;
	    }

	    public String formatMessage(String username) {
	        return username + " " + messageTemplate;
	    }

	    public static NotificationType fromString(String type) {
	        for (NotificationType nt : values()) {
	            if (nt.type.equals(type)) {
	                return nt;
	            }
	        }
	        throw new IllegalArgumentException("Unknown notification type: " + type);
	    }
}
