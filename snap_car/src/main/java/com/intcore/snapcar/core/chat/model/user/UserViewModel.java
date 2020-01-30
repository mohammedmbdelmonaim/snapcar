package com.intcore.snapcar.core.chat.model.user;

public class UserViewModel {

    private final long id;
    private final String firstName;
    private final String email;
    private final String phone;
    private final String imageUrl;
    private final String gender;
    private final String tempPhone;
    private final String resetPhoneCode;

    UserViewModel(long id, String firstName,
                  String email, String phone,
                  String imageUrl, String gender,
                  String resetPhoneCode, String tempPhone) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.imageUrl = imageUrl;
        this.firstName = firstName;
        this.tempPhone = tempPhone;
        this.resetPhoneCode = resetPhoneCode;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTempPhone() {
        return tempPhone;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getResetPhoneCode() {
        return resetPhoneCode;
    }

    @Override
    public boolean equals(Object obj) {
        boolean isEqual = false;
        if (obj == null) return true;
        if (obj instanceof UserViewModel) {
            UserViewModel viewModel = (UserViewModel) obj;
            isEqual = viewModel.getId() == this.id &&
                    viewModel.getImageUrl().contentEquals(this.imageUrl) &&
                    viewModel.getFirstName().contentEquals(this.firstName);
        }
        return isEqual;
    }

}