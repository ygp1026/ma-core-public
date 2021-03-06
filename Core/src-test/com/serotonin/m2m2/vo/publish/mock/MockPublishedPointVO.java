/**
 * @copyright 2018 {@link http://infiniteautomation.com|Infinite Automation Systems, Inc.} All rights reserved.
 * @author Terry Packer
 */
package com.serotonin.m2m2.vo.publish.mock;

import com.serotonin.m2m2.vo.publish.PublishedPointVO;
import com.serotonin.m2m2.web.mvc.rest.v1.model.publisher.AbstractPublishedPointModel;

/**
 *
 * @author Terry Packer
 */
public class MockPublishedPointVO extends PublishedPointVO{

    /* (non-Javadoc)
     * @see com.serotonin.m2m2.vo.publish.PublishedPointVO#asModel()
     */
    @Override
    public AbstractPublishedPointModel<?> asModel() {
        return null;
    }

}
