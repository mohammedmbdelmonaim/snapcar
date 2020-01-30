package com.intcore.snapcar.ui.survey;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.intcore.snapcar.R;
import com.intcore.snapcar.store.model.feedback.FeedbackApiResponse;
import com.intcore.snapcar.store.model.survey.SurveyApiResponse;
import com.intcore.snapcar.core.qualifier.ForActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.ViewHolder> {

    private final LayoutInflater layoutInflater;
    private List<SurveyApiResponse.Questions> questions;
    private Context context;
    private List<QuestionAnswer> questionAnswers = new ArrayList<>();

    public SurveyAdapter(@ForActivity Context context, List<SurveyApiResponse.Questions> viewModels) {
        this.layoutInflater = LayoutInflater.from(context);
        this.questions = viewModels;
        this.context = context;
    }

    @Override
    public SurveyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SurveyAdapter.ViewHolder(layoutInflater.inflate(R.layout.item_survey_question, parent, false));
    }

    @Override
    public void onBindViewHolder(SurveyAdapter.ViewHolder holder, int position) {
        holder.bind(questions.get(position));
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    void addNewQuestion(QuestionAnswer answer) {
        for (int x = 0; x < questionAnswers.size(); x++) {
            if (answer.getQuestionId() == questionAnswers.get(x).getQuestionId()) {
                questionAnswers.remove(x);
            }
        }
        questionAnswers.add(answer);
        Log.d("rwgfrg", String.valueOf(questionAnswers.size()));

    }

    public List<QuestionAnswer> getAnswers() {
        return questionAnswers;
    }

    boolean isEnglishLang() {
        return Locale.getDefault().getLanguage().equals("en");
    }

    public interface OnItemClickListener {
        void onItemClicked(FeedbackApiResponse.FeedBackData brands);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements RatingBar.OnRatingBarChangeListener {

        SurveyApiResponse.Questions question;

        @BindView(R.id.tv_question)
        TextView questionTextView;
        @BindView(R.id.et_answer_text)
        EditText textAnswerEditText;
        @BindView(R.id.rating_bar)
        RatingBar ratingAnswer;
        @BindView(R.id.yes_no_layout)
        LinearLayout yesAndNoLayout;
        @BindView(R.id.multi_choice_layout)
        LinearLayout multiChoiceLayout;
        @BindView(R.id.ra_yes)
        RadioButton radioYes;
        @BindView(R.id.ra_no)
        RadioButton radioNo;
        @BindView(R.id.ra_answer1)
        RadioButton radioAnswer1;
        @BindView(R.id.ra_answer2)
        RadioButton radioAnswer2;
        @BindView(R.id.ra_answer3)
        RadioButton radioAnswer3;
        @BindView(R.id.ra_answer4)
        RadioButton radioAnswer4;
        @BindView(R.id.ra_answer5)
        RadioButton radioAnswer5;
        RadioButton[] radioMultiChoice;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            radioMultiChoice = new RadioButton[]{radioAnswer1, radioAnswer2, radioAnswer3, radioAnswer4, radioAnswer5};
            ratingAnswer.setOnRatingBarChangeListener((ratingBar, v, b) -> addNewQuestion(new QuestionAnswer(question.getSurveyId(), question.getId(), String.valueOf(v))));
            textAnswerEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    addNewQuestion(new QuestionAnswer(question.getSurveyId(), question.getId(), textAnswerEditText.getText().toString()));
                }
            });
        }

        @OnClick({R.id.ra_yes, R.id.ra_no, R.id.ra_answer1, R.id.ra_answer2, R.id.ra_answer3, R.id.ra_answer4, R.id.ra_answer5})
        void onRadiosClicked(View v) {
            switch (v.getId()) {
                case R.id.ra_yes:
                    radioNo.setChecked(false);
                    radioAnswer1.setChecked(false);
                    radioAnswer2.setChecked(false);
                    radioAnswer3.setChecked(false);
                    radioAnswer4.setChecked(false);
                    radioAnswer5.setChecked(false);
                    addNewQuestion(new QuestionAnswer(question.getSurveyId(), question.getId(), "yes"));
                    break;
                case R.id.ra_no:
                    radioYes.setChecked(false);
                    radioAnswer1.setChecked(false);
                    radioAnswer2.setChecked(false);
                    radioAnswer3.setChecked(false);
                    radioAnswer4.setChecked(false);
                    radioAnswer5.setChecked(false);
                    addNewQuestion(new QuestionAnswer(question.getSurveyId(), question.getId(), "no"));
                    break;
                case R.id.ra_answer1:
                    radioNo.setChecked(false);
                    radioYes.setChecked(false);
                    radioAnswer2.setChecked(false);
                    radioAnswer3.setChecked(false);
                    radioAnswer4.setChecked(false);
                    radioAnswer5.setChecked(false);
                    addNewQuestion(new QuestionAnswer(question.getSurveyId(), question.getId(), radioAnswer1.getText().toString()));
                    break;

                case R.id.ra_answer2:
                    radioNo.setChecked(false);
                    radioYes.setChecked(false);
                    radioAnswer1.setChecked(false);
                    radioAnswer3.setChecked(false);
                    radioAnswer4.setChecked(false);
                    radioAnswer5.setChecked(false);
                    addNewQuestion(new QuestionAnswer(question.getSurveyId(), question.getId(), radioAnswer2.getText().toString()));
                    break;

                case R.id.ra_answer3:
                    radioNo.setChecked(false);
                    radioYes.setChecked(false);
                    radioAnswer1.setChecked(false);
                    radioAnswer2.setChecked(false);
                    radioAnswer4.setChecked(false);
                    radioAnswer5.setChecked(false);
                    addNewQuestion(new QuestionAnswer(question.getSurveyId(), question.getId(), radioAnswer3.getText().toString()));
                    break;
                case R.id.ra_answer4:
                    radioNo.setChecked(false);
                    radioYes.setChecked(false);
                    radioAnswer1.setChecked(false);
                    radioAnswer2.setChecked(false);
                    radioAnswer3.setChecked(false);
                    radioAnswer5.setChecked(false);
                    addNewQuestion(new QuestionAnswer(question.getSurveyId(), question.getId(), radioAnswer4.getText().toString()));
                    break;
                case R.id.ra_answer5:
                    radioNo.setChecked(false);
                    radioYes.setChecked(false);
                    radioAnswer1.setChecked(false);
                    radioAnswer2.setChecked(false);
                    radioAnswer3.setChecked(false);
                    radioAnswer4.setChecked(false);
                    addNewQuestion(new QuestionAnswer(question.getSurveyId(), question.getId(), radioAnswer5.getText().toString()));
                    break;

            }
        }

        void bind(SurveyApiResponse.Questions question) {
            this.question = question;
            switch (question.getType()) {
                case 1:
                    yesAndNoLayout.setVisibility(View.VISIBLE);
                    multiChoiceLayout.setVisibility(View.GONE);
                    textAnswerEditText.setVisibility(View.GONE);
                    ratingAnswer.setVisibility(View.GONE);
                    if (isEnglishLang()) {
                        questionTextView.setText(question.getNameEn());
                    } else {
                        questionTextView.setText(question.getNameAr());
                    }
                    break;
                case 2:
                    yesAndNoLayout.setVisibility(View.GONE);
                    multiChoiceLayout.setVisibility(View.GONE);
                    textAnswerEditText.setVisibility(View.GONE);
                    ratingAnswer.setVisibility(View.VISIBLE);
                    if (isEnglishLang()) {
                        questionTextView.setText(question.getNameEn());
                    } else {
                        questionTextView.setText(question.getNameAr());
                    }
                    break;
                case 3:
                    yesAndNoLayout.setVisibility(View.GONE);
                    multiChoiceLayout.setVisibility(View.VISIBLE);
                    textAnswerEditText.setVisibility(View.GONE);
                    ratingAnswer.setVisibility(View.GONE);
                    if (isEnglishLang()) {
                        questionTextView.setText(question.getNameEn());
                    } else {
                        questionTextView.setText(question.getNameAr());
                    }
                    String[] answers = question.getAnswers().split(",");
                    for (int answer = 0; answer < radioMultiChoice.length; answer++) {
                        if (answer < answers.length) {
                            radioMultiChoice[answer].setText(answers[answer].replace("\"", "").replace("[", "").replace("]", ""));
                        } else radioMultiChoice[answer].setVisibility(View.GONE);
                    }

                    break;
                case 4:
                    yesAndNoLayout.setVisibility(View.GONE);
                    multiChoiceLayout.setVisibility(View.GONE);
                    textAnswerEditText.setVisibility(View.VISIBLE);
                    ratingAnswer.setVisibility(View.GONE);
                    if (isEnglishLang()) {
                        questionTextView.setText(question.getNameEn());
                    } else {
                        questionTextView.setText(question.getNameAr());
                    }
                    break;
            }

        }

        @Override
        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

        }
    }

}
