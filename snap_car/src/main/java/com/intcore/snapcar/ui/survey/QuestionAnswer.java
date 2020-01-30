package com.intcore.snapcar.ui.survey;

public class QuestionAnswer {

    private int surveys_id;
    private int question_id;
    private String answer;

    public QuestionAnswer(int surveyId, int questionId, String answer) {
        this.surveys_id = surveyId;
        this.question_id = questionId;
        this.answer = answer;
    }

    public int getSurveyId() {
        return surveys_id;
    }

    public int getQuestionId() {
        return question_id;
    }

    public String getAnswer() {
        return answer;
    }
}
