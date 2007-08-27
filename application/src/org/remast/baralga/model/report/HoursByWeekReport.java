package org.remast.baralga.model.report;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.joda.time.DateTime;
import org.remast.baralga.gui.events.ProTrackEvent;
import org.remast.baralga.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;
import org.remast.baralga.model.utils.ProTrackUtils;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

public class HoursByWeekReport implements Observer {

    private PresentationModel model;
    
    EventList<HoursByWeek> hoursByWeekList;
    
    private Filter<ProjectActivity> filter;

    /**
     * @param filter the filter to set
     */
    public void setFilter(Filter<ProjectActivity> filter) {
        this.filter = filter;
        calculateHours();
    }

    public HoursByWeekReport(PresentationModel model) {
        this.model = model;
        this.model.addObserver(this);
        
        this.hoursByWeekList = new BasicEventList<HoursByWeek>();
        calculateHours();
    }
    
    public void calculateHours() {
        this.hoursByWeekList.clear();

        List<ProjectActivity> filteredActivities = getFilteredActivities();
        for (ProjectActivity activity : filteredActivities) {
            this.addHours(activity);
        }
    }
    
    public void addHours(final ProjectActivity activity) {
        if(filter != null && !filter.satisfiesPredicates(activity))
            return;
        
        DateTime dateTime = new DateTime(activity.getStart());
        
        HoursByWeek newHoursByWeek = new HoursByWeek(
                dateTime.getWeekOfWeekyear(), 
                ProTrackUtils.calculateDuration(activity)
        );
        
        if (this.hoursByWeekList.contains(newHoursByWeek)) {
            HoursByWeek hoursByWeek = this.hoursByWeekList.get(hoursByWeekList.indexOf(newHoursByWeek));
            hoursByWeek.addHours(newHoursByWeek.getHours());
        } else {
            this.hoursByWeekList.add(newHoursByWeek);
        }

    }

    public EventList<HoursByWeek> getHoursByWeek() {
        return hoursByWeekList;
    }
    
    /**
     * Get all filtered acitivies.
     * @return all activies after applying the filter.
     */
    private List<ProjectActivity> getFilteredActivities() {
        List<ProjectActivity> filteredActivitiesList = new Vector<ProjectActivity>();

        if (filter != null)
            filteredActivitiesList.addAll(filter.applyFilters(this.model.getActivitiesList()));
        else
            filteredActivitiesList.addAll(this.model.getActivitiesList());

        return filteredActivitiesList;
    }

    public void update(Observable source, Object eventObject) {
        ProTrackEvent event = (ProTrackEvent) eventObject;
        switch (event.getType()) {

        case ProTrackEvent.PROJECT_ACTIVITY_ADDED:
            ProjectActivity activity = (ProjectActivity) event.getData();
            addHours(activity);
            break;

        case ProTrackEvent.PROJECT_ACTIVITY_REMOVED:
            calculateHours();
            break;
            
        case ProTrackEvent.PROJECT_ACTIVITY_CHANGED:
            // TODO: Replace calculation by remove+add.
            calculateHours();
            break;
        }
    }

}