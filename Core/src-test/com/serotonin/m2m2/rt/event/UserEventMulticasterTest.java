/**
 * Copyright (C) 2018 Infinite Automation Software. All rights reserved.
 */
package com.serotonin.m2m2.rt.event;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.serotonin.m2m2.i18n.TranslatableMessage;
import com.serotonin.m2m2.rt.event.type.EventType.DuplicateHandling;
import com.serotonin.m2m2.rt.event.type.MockEventType;
import com.serotonin.m2m2.vo.User;

/**
 * Test the logic of the multicaster
 * @author Terry Packer
 */
public class UserEventMulticasterTest {

    @Test
    public void testMulticastEventsForUser() {
        
        int dataPointId = 1;
        int eventCount = 10000;
        int userCount = 100;
        AtomicInteger idCounter = new AtomicInteger(1);
        
        List<User> users = createUsers(userCount, 0, idCounter);
        List<MockUserEventListener> listeners = new ArrayList<>();
        UserEventListener multicaster = null;
        for(User u : users) {
            MockUserEventListener l = new MockUserEventListener(u);
            listeners.add(l);
            multicaster = UserEventMulticaster.add(l, multicaster);
        }
        

        List<EventInstance> events = new ArrayList<>();
        long time = 0;
        for(int i=0; i<eventCount; i++) {
            EventInstance event = createMockEventInstance(i, dataPointId, time);
            events.add(event);
            multicaster.raised(event);
            time += 1;
        }

        //Ack
        for(EventInstance e : events)
            multicaster.acknowledged(e);
        
        //Rtn
        for(EventInstance e : events)
            multicaster.returnToNormal(e);
        
        //Confirm all 100 saw 10000 were raised
        for(MockUserEventListener l : listeners)
            assertEquals(eventCount, l.getRaised().size());
        
        //Confirm all 100 saw 10000 were acked
        for(MockUserEventListener l : listeners)
            assertEquals(eventCount, l.getAcknowledged().size());
        
        //Confirm all 100 saw 10000 were rtned
        for(MockUserEventListener l : listeners)
            assertEquals(eventCount, l.getReturned().size());
    }
    
    @Test
    public void testMulticastEventsForUsersWithPermissions() {
        
        int dataPointId = 1;
        int eventCount = 10000;
        int userCount = 5*6;
        AtomicInteger idCounter = new AtomicInteger(1);
        
        //Add them out of order so the tree is jumbled with permissions hither and yon
        List<User> users = createUsers(userCount/6, 0, idCounter);
        users.addAll(createUsers(userCount/6, 1, idCounter));
        users.addAll(createUsers(userCount/6, 2, idCounter));
        users.addAll(createUsers(userCount/6, 0, idCounter));
        users.addAll(createUsers(userCount/6, 1, idCounter));
        users.addAll(createUsers(userCount/6, 2, idCounter));
        List<Integer> idsToNotify = new ArrayList<>();
        List<MockUserEventListener> listeners = new ArrayList<>();
        UserEventListener multicaster = null;
        MockEventType mockEventType =  new MockEventType(DuplicateHandling.ALLOW, null, 0, dataPointId);
        for(User u : users) {
            MockUserEventListener l = new MockUserEventListener(u);
            if(mockEventType.hasPermission(u)) //This work is normally done by the event manager handling the raiseEvent calls
                idsToNotify.add(u.getId());    // through an EventNotifyWorkItem
            listeners.add(l);
            multicaster = UserEventMulticaster.add(l, multicaster);
        }
        

        List<EventInstance> events = new ArrayList<>();
        long time = 0;
        for(int i=0; i<eventCount; i++) {
            EventInstance event = createMockEventInstance(i, dataPointId, time);
            events.add(event);
            event.setIdsToNotify(idsToNotify);
            multicaster.raised(event);
            time += 1;
        }

        //Ack
        for(EventInstance e : events)
            multicaster.acknowledged(e);
        
        //Rtn
        for(EventInstance e : events)
            multicaster.returnToNormal(e);
        
        //Confirm those with permissions saw all 10000 raised
        for(MockUserEventListener l : listeners) {
            if(!"".equals(l.getUser().getPermissions()))
                assertEquals(eventCount, l.getRaised().size());
            else
                assertEquals(0, l.getRaised().size());
        }
        
        //Confirm those with permissions saw all 10000 acked
        for(MockUserEventListener l : listeners) {
            if(!"".equals(l.getUser().getPermissions()))
                assertEquals(eventCount, l.getAcknowledged().size());
            else
                assertEquals(0, l.getRaised().size());
        }
        
        //Confirm those with permissions saw all 10000 rtned
        for(MockUserEventListener l : listeners) {
            if(!"".equals(l.getUser().getPermissions()))
                assertEquals(eventCount, l.getReturned().size());
            else
                assertEquals(0, l.getRaised().size());
        }
    }
    
    public EventInstance createMockEventInstance(int id, int dataPointId, long time) {
        MockEventType type = new MockEventType(DuplicateHandling.ALLOW, null, id, dataPointId);
        return new EventInstance(type, time, true, AlarmLevels.URGENT, 
                new TranslatableMessage("common.default", "Mock Event " + id), null);
    }
    
    public List<User> createUsers(int size, int permType, AtomicInteger idCounter) {
        List<User> users = new ArrayList<>();
        for(int i=0; i<2; i++) {
            User user = new User();
            user.setId(idCounter.getAndIncrement());
            user.setName("User" + i);
            user.setUsername("user" + i);
            user.setPassword("password");
            user.setEmail("user" + i + "@yourMangoDomain.com");
            user.setPhone("");
            if(permType == 0)
                user.setPermissions("superadmin");
            else if(permType == 1)
                user.setPermissions("MOCK");
            else
                user.setPermissions("");
            user.setDisabled(false);
            users.add(user);
        }
        return users;
    }
    
}
