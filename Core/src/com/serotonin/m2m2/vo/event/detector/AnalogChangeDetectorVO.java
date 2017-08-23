/**
 * Copyright (C) 2016 Infinite Automation Software. All rights reserved.
 * @author Terry Packer
 */
package com.serotonin.m2m2.vo.event.detector;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.json.spi.JsonProperty;
import com.serotonin.m2m2.DataTypes;
import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.event.detectors.AbstractEventDetectorRT;
import com.serotonin.m2m2.rt.event.detectors.AnalogChangeDetectorRT;
import com.serotonin.m2m2.view.text.TextRenderer;

/**
 * TODO Class is a work in progress, not wired in or tested yet.
 *      This will require uncommenting the line in the ModuleRegistry
 *      pertaining to this detector's definition
 * 
 * @author Terry Packer
 *
 */
public class AnalogChangeDetectorVO extends TimeoutDetectorVO<AnalogChangeDetectorVO>{

	private static final long serialVersionUID = 1L;
	
	//Maximum change allowed before firing event
	@JsonProperty
	private double limit; 
	@JsonProperty
	private boolean checkIncrease = true;
	@JsonProperty
    private boolean checkDecrease = true;
	

	public AnalogChangeDetectorVO() {
		super(new int[] { DataTypes.NUMERIC });
	}
	
	public double getLimit() {
		return limit;
	}

	public void setLimit(double limit) {
		this.limit = limit;
	}

	public boolean isCheckIncrease() {
		return checkIncrease;
	}

	public void setCheckIncrease(boolean checkIncrease) {
		this.checkIncrease = checkIncrease;
	}
	
	public boolean isCheckDecrease() {
        return checkDecrease;
    }

    public void setCheckDecrease(boolean checkDecrease) {
        this.checkDecrease = checkDecrease;
    }

	/* (non-Javadoc)
	 * @see com.serotonin.m2m2.vo.event.detector.AbstractEventDetectorVO#createRuntime()
	 */
	@Override
	public AbstractEventDetectorRT<AnalogChangeDetectorVO> createRuntime() {
		return new AnalogChangeDetectorRT(this);
	}

	/* (non-Javadoc)
	 * @see com.serotonin.m2m2.vo.event.detector.AbstractEventDetectorVO#getConfigurationDescription()
	 */
	@Override
	protected TranslatableMessage getConfigurationDescription() {
		String prettyLimit = njbGetDataPoint().getTextRenderer().getText(limit, TextRenderer.HINT_SPECIFIC);
		TranslatableMessage durationDescription = getDurationDescription();
		
		if(checkIncrease && checkDecrease)
            return new TranslatableMessage("event.detectorVo.analogChangePeriod", prettyLimit, durationDescription);
        else if(checkIncrease)
            return new TranslatableMessage("event.detectorVo.analogIncreasePeriod", prettyLimit, durationDescription);
        else if(checkDecrease)
            return new TranslatableMessage("event.detectorVo.analogDecreasePeriod", prettyLimit, durationDescription);
        else
            throw new ShouldNeverHappenException("Illegal state for analog change detector" + xid);
	}

}
