package com.campustribune.beans;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by aditi on 16/07/16.
 */
public class EventComment implements Serializable {

    private UUID id;
    private UUID eventId;
    private String createdBy;
    private String createdOn;
    private String comment;
    private String reportedBy;

    public EventComment(){}

    public EventComment(String comment, String createdBy){
        this.comment = comment;
        this.createdBy = createdBy;
        DateTime dt = new DateTime(DateTimeZone.UTC);
        this.createdOn = dt.toString(ISODateTimeFormat.dateTime().withZoneUTC());
    }

    public EventComment(UUID id, UUID eventId, String createdBy, String createdOn, String comment, String reportedBy) {
        this.id = id;
        this.eventId = eventId;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
        this.comment = comment;
        this.reportedBy = reportedBy;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventComment that = (EventComment) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (eventId != null ? !eventId.equals(that.eventId) : that.eventId != null) return false;
        if (createdBy != null ? !createdBy.equals(that.createdBy) : that.createdBy != null)
            return false;
        if (createdOn != null ? !createdOn.equals(that.createdOn) : that.createdOn != null)
            return false;
        if (comment != null ? !comment.equals(that.comment) : that.comment != null) return false;
        return !(reportedBy != null ? !reportedBy.equals(that.reportedBy) : that.reportedBy != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (eventId != null ? eventId.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (createdOn != null ? createdOn.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (reportedBy != null ? reportedBy.hashCode() : 0);
        return result;
    }
}
