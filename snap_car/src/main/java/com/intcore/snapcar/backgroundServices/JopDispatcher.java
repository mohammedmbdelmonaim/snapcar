package com.intcore.snapcar.backgroundServices;

import android.content.Context;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.intcore.snapcar.core.qualifier.ForApplication;

import javax.inject.Inject;

public class JopDispatcher {

    private final Context context;
    private FirebaseJobDispatcher firebaseJobDispatcher;

    @Inject
    public JopDispatcher(@ForApplication Context context) {
        this.context = context;
    }

    public Job createJob(FirebaseJobDispatcher dispatcher) {
        return dispatcher.newJobBuilder()
                // persist the task across boots
                .setLifetime(Lifetime.FOREVER)
                // Call this service when the criteria are met.
                .setService(ScheduledJobService.class)
                // unique id of the task
                .setTag("LocationJob")
//                 We are mentioning that the job is not periodic.
//                .setRecurring(true)
                // Run between 0 - 60 seconds from now.
//                .setTrigger(Trigger.executionWindow(0, 60))
//                 retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                //Run this job only when the network is avaiable.
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();
    }

    public FirebaseJobDispatcher getInstance() {
        if (firebaseJobDispatcher == null) {
            firebaseJobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        }
        return firebaseJobDispatcher;
    }

    public void inject(ScheduledJobService scheduledJobService) {
    }
}