/**
 * Copyright (C) 2015 Infinite Automation Software. All rights reserved.
 * @author Terry Packer
 */
package com.serotonin.m2m2.vo.comment;

import com.serotonin.m2m2.db.dao.AbstractDao;
import com.serotonin.m2m2.db.dao.UserCommentDao;
import com.serotonin.m2m2.i18n.ProcessResult;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.util.ExportCodes;
import com.serotonin.m2m2.vo.AbstractActionVO;
import com.serotonin.m2m2.web.taglib.Functions;
import com.serotonin.validation.StringValidation;

/**
 * Container for User Comments 
 * @author Terry Packer
 *
 */
public class UserCommentVO extends AbstractActionVO<UserCommentVO>{
	
	public static final ExportCodes COMMENT_TYPE_CODES = new ExportCodes();
    public static final int TYPE_EVENT = 1;
    public static final int TYPE_POINT = 2;
    public static final int TYPE_JSON_DATA = 3;
	static{
		COMMENT_TYPE_CODES.addElement(TYPE_POINT, "POINT");
		COMMENT_TYPE_CODES.addElement(TYPE_EVENT, "EVENT");
		COMMENT_TYPE_CODES.addElement(TYPE_JSON_DATA, "JSON_DATA");
	}

	private static final long serialVersionUID = 1L;

    // Configuration fields
    private int userId;
    private long ts;
    private String comment;
    
    private int commentType; //
    private int referenceId; //The ID of the Item being commented on

    // Relational fields
    private String username;
    
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCommentType() {
		return commentType;
	}

	public void setCommentType(int commentType) {
		this.commentType = commentType;
	}

	public int getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(int referenceId) {
		this.referenceId = referenceId;
	}

	public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPrettyTime() {
        return Functions.getTime(ts);
    }
    
	/* (non-Javadoc)
	 * @see com.serotonin.m2m2.util.ChangeComparable#getTypeKey()
	 */
	@Override
	public String getTypeKey() {
		return "event.audit.userComment";
	}
	
    @Override
    public void validate(ProcessResult response) {
        //Don't do super validate as we don't even have those properties!
        //xid,name in superclass
        if (StringValidation.isLengthGreaterThan(comment, 1024))
            response.addMessage("comment", new TranslatableMessage("validate.notLongerThan", 255));

        if (!COMMENT_TYPE_CODES.isValidId(commentType))
            response.addContextualMessage("commentType", "validate.invalidValueWithAcceptable", COMMENT_TYPE_CODES.getCodeList());

    }

	/* (non-Javadoc)
	 * @see com.serotonin.m2m2.vo.AbstractVO#getDao()
	 */
	@Override
	protected AbstractDao<UserCommentVO> getDao() {
		return UserCommentDao.getInstance();
	}

}
