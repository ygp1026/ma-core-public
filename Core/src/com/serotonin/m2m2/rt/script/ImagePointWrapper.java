package com.serotonin.m2m2.rt.script;

import javax.script.ScriptEngine;

import com.serotonin.m2m2.rt.dataImage.IDataPointValueSource;
import com.serotonin.m2m2.rt.dataImage.PointValueTime;
import com.serotonin.m2m2.rt.dataImage.types.ImageValue;
import com.serotonin.m2m2.util.DateUtils;

public class ImagePointWrapper extends AbstractPointWrapper {

    ImagePointWrapper(IDataPointValueSource point, ScriptEngine engine,
            ScriptPointValueSetter setter) {
        super(point, engine, setter);
    }
    
    public byte[] getValue() { //always use cache here, last() for no cache
        PointValueTime value = point.getPointValue();
        if(value == null)
            return null;
        return ((ImageValue)value.getValue()).getData();
    }
    
    public byte[] ago(int periodType) {
        return ago(periodType, 1, false);
    }
    
    public byte[] ago(int periodType, boolean cache) {
        return ago(periodType, 1, cache);
    }
    
    public byte[] ago(int periodType, int periods) {
        return ago(periodType, periods, false);
    }
    
    public byte[] ago(int periodType, int periods, boolean cache) {
        long from = DateUtils.minus(getContext().getRuntime(), periodType, periods);
        PointValueTime pvt;
        if(cache)
            pvt = point.getPointValueBefore(from);
        else
            pvt = valueFacade.getPointValueBefore(from);
        
        if (pvt == null)
            return null;
        return ((ImageValue)pvt.getValue()).getData();
    }

    @Override
    protected void helpImpl(StringBuilder builder) {
        builder.append("ago(periodType, cache): byte[],\n ");      
        builder.append("ago(periodType, periods, cache): byte[],\n ");
    }

}
