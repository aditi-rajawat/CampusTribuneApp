package com.campustribune.event.utility;

import com.campustribune.beans.Event;
import com.campustribune.beans.EventUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by aditi on 30/07/16.
 */
public class UpdateEventUserActions {

    private ArrayList<Event> listOfEvents;
    private EventUser eventUser;

    public UpdateEventUserActions(ArrayList<Event> events, EventUser eventUser){
        this.listOfEvents = events;
        this.eventUser = eventUser;
    }

    public void updateAll(){
        setFollowingEvents();
        setUpvotedEvents();
        setDownvotedEvents();
        setGoingEvents();
        setNotGoingEvents();
    }

    private void setFollowingEvents(){
        if(this.eventUser!=null){
            List<UUID> followingEvents = eventUser.getFollowingEvents();
            if(followingEvents!=null && followingEvents.size()>0){
                for(UUID id: followingEvents){
                    int index = searchEventInList(id);
                    if(index != -1){
                        Event event = listOfEvents.get(index);
                        listOfEvents.remove(index);
                        event.setFollow(true);
                        listOfEvents.add(index, event);
                    }
                }
            }
        }
    }

    private void setUpvotedEvents(){
        if(this.eventUser!=null){
            List<UUID> upvotedEvents = eventUser.getUpVotedEvents();
            if(upvotedEvents!=null && upvotedEvents.size()>0){
                for(UUID id: upvotedEvents){
                    int index = searchEventInList(id);
                    if(index != -1){
                        Event event = listOfEvents.get(index);
                        listOfEvents.remove(index);
                        event.setUpvoted(true);
                        listOfEvents.add(index, event);
                    }
                }
            }
        }
    }

    private void setDownvotedEvents(){
        if(this.eventUser!=null){
            List<UUID> downvotedEvents = eventUser.getDownVotedEvents();
            if(downvotedEvents!=null && downvotedEvents.size()>0){
                for(UUID id: downvotedEvents){
                    int index = searchEventInList(id);
                    if(index != -1){
                        Event event = listOfEvents.get(index);
                        listOfEvents.remove(index);
                        event.setDownvoted(true);
                        listOfEvents.add(index, event);
                    }
                }
            }
        }
    }

    private void setGoingEvents(){
        if(this.eventUser!=null){
            List<UUID> goingEvents = eventUser.getGoingEvents();
            if(goingEvents!=null && goingEvents.size()>0){
                for(UUID id: goingEvents){
                    int index = searchEventInList(id);
                    if(index != -1){
                        Event event = listOfEvents.get(index);
                        listOfEvents.remove(index);
                        event.setGoing(true);
                        listOfEvents.add(index, event);
                    }
                }
            }
        }
    }

    private void setNotGoingEvents(){
        if(this.eventUser!=null){
            List<UUID> notgoingEvents = eventUser.getNotgoingEvents();
            if(notgoingEvents!=null && notgoingEvents.size()>0){
                for(UUID id: notgoingEvents){
                    int index = searchEventInList(id);
                    if(index != -1){
                        Event event = listOfEvents.get(index);
                        listOfEvents.remove(index);
                        event.setNotGoing(true);
                        listOfEvents.add(index, event);
                    }
                }
            }
        }
    }

    private int searchEventInList(UUID id){
        if(listOfEvents!=null && listOfEvents.size()>0){
            for(int index=0; index< listOfEvents.size(); index++){
                Event each = listOfEvents.get(index);
                if(each.getId().equals(id)){
                    return index;
                }
            }
        }
        return -1;
    }
}
