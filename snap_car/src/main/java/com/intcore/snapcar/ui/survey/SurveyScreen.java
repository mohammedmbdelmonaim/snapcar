package com.intcore.snapcar.ui.survey;

import com.intcore.snapcar.store.model.survey.SurveyApiResponse;
import com.intcore.snapcar.core.base.BaseActivityScreen;

import okhttp3.ResponseBody;

public interface SurveyScreen extends BaseActivityScreen{
    void updateUi(SurveyApiResponse surveyApiResponse);

    void surveyIsSent(ResponseBody responseBody);

    void processLogout();
}
