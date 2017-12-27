package com.friendly.walking.service;


import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.friendly.walking.util.JWLog;

public class JobWalkingService extends JobService {

    private static final String TAG = "JobWalkingService";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        JWLog.d(TAG, "Performing long running task in scheduled job :"+jobParameters);
        // TODO(developer): add long running task here.
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

}