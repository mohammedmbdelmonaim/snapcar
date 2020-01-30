package com.intcore.snapcar.core.util;

import android.content.Context;
import android.text.TextUtils;


import com.intcore.snapcar.R;

import java.util.Locale;
import java.util.regex.Pattern;

public class TextUtil {

    private final Context context;

    public TextUtil(Context context) {
        Preconditions.checkNonNull(context, "should not pass null context reference");
        this.context = context;
    }

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param text the string to be examined
     * @return true if str is null or zero length
     */
    public boolean isEmpty(String text) {
        return TextUtils.isEmpty(text);
    }

    /**
     * Returns true if the string matches a human name format
     *
     * @param name the string to be examined
     * @return true if name consists of at least three alphabetic characters with no white space
     * see https://stackoverflow.com/a/3802238/7330512
     */
    public boolean isValidName(String name) {
        Preconditions.checkNonNull(name);
        String expression = "^[a-zA-Z\u0621-\u064A\u0660-\u0669\\s]+$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        return pattern.matcher(name).matches();
    }

    /**
     * @param email the string to be examined
     * @return true if email is valid
     * see https://stackoverflow.com/a/3802238/7330512
     */
    public boolean isValidEmail(String email) {
        Preconditions.checkNonNull(email);
        String expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        return pattern.matcher(email).matches();
    }

    /**
     * @param password the string to be examined
     * @return true if password consists of at at least 6 chars the contains at least 1 number, 1 small letter and 1 capital letter
     * see https://stackoverflow.com/a/3802238/7330512
     */
    public boolean isValidPassword(String password) {
        Preconditions.checkNonNull(password);
        Preconditions.checkNonNull(password);
        String expression = "^(?=.*[0-9])(?=.*[A-Z]).{6,}$";
        Pattern pattern = Pattern.compile(expression);
        return pattern.matcher(password).matches();
    }

    /**
     * @param number     the string to be examined
     * @param phoneRegex
     * @return true if valid phone number
     * see https://howtodoinjava.com/regex/java-regex-validate-international-phone-numbers/
     */
    public boolean isValidPhoneNumber(String number, String phoneRegex) {
        Preconditions.checkNonNull(number);
        String expression = "^[+]?[0-9]{" + phoneRegex + "," + phoneRegex + "}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        return pattern.matcher(number).matches();
    }

    /**
     * @param area the string to be examined
     * @return true if valid Area shouldn't contain special character and number only
     */
    public boolean isValidArea(String area) {
        String expression = "^[a-zA-Z\u0621-\u064A\u0660-\u0669\\s]+$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        return pattern.matcher(area).matches();
    }

    /**
     * @param text the string to be examined
     *             <p>
     *             Returns Result object containing boolean variable isValid, messageEn and localized messageEn
     */
    public Result getIfNotEmptyStringResult(String text) {
        Result result = new Result();
        if (TextUtils.isEmpty(text)) {
            result.valid = false;
            result.messageEn = context.getString(R.string.please_fill_data);
            result.messageAr = "من فضلك ادخل جميع البيانات";
        } else {
            result.valid = true;
            result.messageEn = "";
            result.messageAr = "";
        }
        return result;
    }

    /**
     * @param name the string to be examined
     *             <p>
     *             Returns Result object containing boolean variable isValid, messageEn and localized messageEn
     */
    public Result getIfValidNameResult(String name) {
        Preconditions.checkNonNull(name);
        Result result = new Result();

        if (isEmpty(name)) {
            return getIfNotEmptyStringResult(name);
        } else if (isValidName(name)) {
            result.valid = true;
            result.messageEn = "";
            result.messageAr = "";
        } else {
            result.valid = false;
            result.messageEn = context.getString(R.string.invalid_name);
            result.messageAr = "الاسم غير صحيح";
        }
        return result;
    }

    /**
     * @param email the string to be examined
     *              <p>
     *              Returns Result object containing boolean variable isValid, messageEn and localized messageEn
     */
    public Result getIfValidEmailResult(String email) {
        Preconditions.checkNonNull(email);
        Result result = new Result();

        if (isEmpty(email)) {
            return getIfNotEmptyStringResult(email);
        } else if (isValidEmail(email)) {
            result.valid = true;
            result.messageEn = "";
            result.messageAr = "";
        } else {
            result.valid = false;
            result.messageEn = context.getString(R.string.invalid_email);
            result.messageAr = "عنوان البريد الإلكتروني غير صالح";
        }
        return result;
    }

    /**
     * @param password the string to be examined
     *                 <p>
     *                 Returns Result object containing boolean variable isValid, messageEn and localized messageEn
     */
    public Result getIfValidPasswordResult(String password) {
        Preconditions.checkNonNull(password);
        Result result = new Result();
        if (isEmpty(password)) {
            return getIfNotEmptyStringResult(password);
        } else if (isValidPassword(password)) {
            result.valid = true;
            result.messageEn = "";
            result.messageAr = "";
        } else {
            result.valid = false;
            result.messageEn = context.getString(R.string.password_validation);
            result.messageAr = " يجب أن تكون كلمة المرور من 6 أحرف على الأقل وتحتوي على حرف كبير وحرف صغير";
        }
        return result;
    }

    /**
     * @param password  the string to be examined
     * @param password2 the string to check against
     *                  <p>
     *                  Returns Result object containing boolean variable isValid, messageEn and localized messageEn
     */
    public Result getIfPasswordsMatchResult(String password, String password2) {
        Preconditions.checkNonNull(password);
        Preconditions.checkNonNull(password2);
        Result result = new Result();
        if (password.contentEquals(password2)) {
            result.valid = true;
            result.messageEn = "";
            result.messageAr = "";
        } else {
            result.valid = false;
            result.messageEn = context.getString(R.string.password_dont_match);
            result.messageAr = "كلمة المرور غير متطابقة";
        }
        return result;
    }

    /**
     * @param number     the string to be examined
     *                   <p>
     *                   Returns Result object containing boolean variable isValid, messageEn and localized messageEn
     * @param phoneRegex
     */
    public Result getIfValidPhoneNumberResult(String number, String phoneRegex) {
        Preconditions.checkNonNull(number);
        Result result = new Result();
        if (isEmpty(number)) {
            return getIfNotEmptyStringResult(number);
        } else if (isValidPhoneNumber(number, phoneRegex)) {
            result.valid = true;
            result.messageEn = "";
            result.messageAr = "";
        } else {
            result.valid = false;
            result.messageEn = context.getString(R.string.invalid_phone_number);
            result.messageAr = "رقم الجوال غير صحيح";
        }
        return result;
    }

    /**
     * @param area the string to be examined
     *             <p>
     *             Returns Result object containing boolean variable isValid, messageEn and localized messageEn
     */
    public Result getIfValidAreaResult(String area) {
        Preconditions.checkNonNull(area);
        Result result = new Result();
        if (isEmpty(area)) {
            return getIfNotEmptyStringResult(area);
        } else if (isValidArea(area)) {
            result.valid = true;
            result.messageEn = "";
            result.messageAr = "";
        } else {
            result.valid = false;
            result.messageEn = context.getString(R.string.invalid_area);
            result.messageAr = " اسم المنطقة غير صحيح";
        }
        return result;
    }

    public String convertSecondsToMinutes(int totalSecs) {
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;
        return String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds);
    }

    public static final class Result {

        boolean valid;
        String messageEn;
        String messageAr;

        public Result() {
            this.messageAr = "من فضلك ادخل جميع البيانات";
            if (Locale.getDefault().getLanguage().equals("en")) {
                this.messageEn = "Please fill in all required data";
            } else {
                this.messageEn = "من فضلك ادخل جميع البيانات";
            }
            this.valid = false;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessageEn() {
            return messageEn;
        }

        public String getMessageAr() {
            return messageAr;
        }

    }
}