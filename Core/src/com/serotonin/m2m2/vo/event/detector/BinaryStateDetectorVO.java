/**
 * Copyright (C) 2016 Infinite Automation Software. All rights reserved.
 * @author Terry Packer
 */
package com.serotonin.m2m2.vo.event.detector;

import com.serotonin.json.spi.JsonProperty;
import com.serotonin.m2m2.DataTypes;
import com.serotonin.m2m2.db.dao.DataPointDao;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.event.detectors.AbstractEventDetectorRT;
import com.serotonin.m2m2.rt.event.detectors.BinaryStateDetectorRT;
import com.serotonin.m2m2.view.text.TextRenderer;

/**
 * @author Terry Packer
 *
 */
public class BinaryStateDetectorVO extends TimeoutDetectorVO<BinaryStateDetectorVO>{

	private static final long serialVersionUID = 1L;
	
	@JsonProperty
	private boolean state;
	
	public BinaryStateDetectorVO() {
		super(new int[] { DataTypes.BINARY });
	}
	
	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	/* (non-Javadoc)
	 * @see com.serotonin.m2m2.vo.event.detector.AbstractEventDetectorVO#createRuntime()
	 */
	@Override
	public AbstractEventDetectorRT<BinaryStateDetectorVO> createRuntime() {
		return new BinaryStateDetectorRT(this);
	}

	/* (non-Javadoc)
	 * @see com.serotonin.m2m2.vo.event.detector.AbstractEventDetectorVO#getConfigurationDescription()
	 */
	@Override
	protected TranslatableMessage getConfigurationDescription() {
	    if(dataPoint == null)
            dataPoint = DataPointDao.getInstance().getDataPoint(sourceId);
        TranslatableMessage durationDesc = getDurationDescription();

        if (durationDesc == null)
            return new TranslatableMessage("event.detectorVo.state", dataPoint.getTextRenderer().getText(
                    state, TextRenderer.HINT_SPECIFIC));
        return new TranslatableMessage("event.detectorVo.statePeriod", dataPoint.getTextRenderer().getText(
                    state, TextRenderer.HINT_SPECIFIC), durationDesc);
	}
	
	

}
