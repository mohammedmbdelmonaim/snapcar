package com.intcore.snapcar.ui.survey;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.GsonBuilder;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.model.survey.SurveyApiResponse;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.core.base.BaseActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;

public class SurveyActivity extends BaseActivity implements SurveyScreen {

    @BindView(R.id.rv_questions)
    RecyclerView recyclerViewQuestions;
    @BindView(R.id.iv_back)
    ImageView backImageView;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    @Inject
    SurveyPresenter presenter;
    private SurveyAdapter surveyAdapter;
    private String surveyId;

    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);

        ButterKnife.bind(this);
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        backImageView.setImageDrawable(icons.getDrawable(0));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_survey;
    }

    @Override
    public void updateUi(SurveyApiResponse surveyApiResponse) {
        if (surveyApiResponse.getSurvies().get(0) != null) {
            if (surveyApiResponse.getSurvies().get(0).getQuestions() != null) {
                this.surveyId = String.valueOf(surveyApiResponse.getSurvies().get(0).getId());
                recyclerViewQuestions.setLayoutManager(new LinearLayoutManager(this));
                surveyAdapter = new SurveyAdapter(this, surveyApiResponse.getSurvies().get(0).getQuestions());
                recyclerViewQuestions.setAdapter(surveyAdapter);
            }
        }
    }

    @Override
    public void surveyIsSent(ResponseBody responseBody) {
        showSuccessMessage(getString(R.string.survey_sent));
        finish();
    }

    @Override
    public void processLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.done_btn)
    public void onDoneClicked() {
        if (surveyAdapter != null) {
            List<QuestionAnswer> answers = surveyAdapter.getAnswers();
            if (answers.size() == 0) {
                showWarningMessage(getString(R.string.must_answer_two_question_at_least));
                return;
            }
            String json = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().
                    create().toJson(answers);
            presenter.postSurvey(surveyId, json);
        }
    }

    @Override
    public void showErrorMessage(String message) {
        getUiUtil().getErrorSnackBar(snackBarContainer, message).show();
    }

    @Override
    public void showSuccessMessage(String message) {
        getUiUtil().getSuccessSnackBar(snackBarContainer, message).show();
    }

    @Override
    public void showWarningMessage(String message) {
        getUiUtil().getWarningSnackBar(snackBarContainer, message).show();
    }

    @OnClick(R.id.iv_back)
    public void onBackClicked() {
        finish();
    }
}