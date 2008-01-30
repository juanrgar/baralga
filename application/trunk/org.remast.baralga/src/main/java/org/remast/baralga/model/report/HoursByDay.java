package org.remast.baralga.model.report;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

public class HoursByDay {
    
    /** The week of the year. */
    private Date day;
    
    /** The amount of hours worked that week. */
    private double hours;
    
    public HoursByDay(Date day, double hours) {
        this.day = day;
        this.hours = hours;
    }

    /**
     * @return the week
     */
    public Date getDay() {
        return day;
    }

    /**
     * @return the hours
     */
    public double getHours() {
        return hours;
    }
    
    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (!(that instanceof HoursByDay)) {
            return false;
        }

        final HoursByDay accAct = (HoursByDay) that;
        return DateUtils.isSameDay(this.getDay(), accAct.getDay());
    }

    /**
     * Adds the given hours to the hours on that day.
     * @param additionalHours the hours to add
     */
    public void addHours(double additionalHours) {
        this.hours += additionalHours;
    }

}