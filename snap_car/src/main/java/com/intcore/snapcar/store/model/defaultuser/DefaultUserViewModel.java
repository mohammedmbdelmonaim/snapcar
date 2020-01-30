package com.intcore.snapcar.store.model.defaultuser;

import com.intcore.snapcar.store.model.showroom.ShowRoomInfoViewModel;

public class DefaultUserViewModel {

    private final long id;
    private final String email;
    private final String phone;
    private final String gender;
    private final String website;
    private final String address;
    private final String imageUrl;
    private final String coverUrl;
    private final String firstName;
    private final String birthYear;
    private final String tempPhone;
    private final String resetPhoneCode;
    private final ShowRoomInfoViewModel showRoomInfoViewModel;

    DefaultUserViewModel(long id,
                         String email,
                         String phone,
                         String gender,
                         String website,
                         String address,
                         String imageUrl,
                         String coverUrl,
                         String firstName,
                         String birthYear,
                         String tempPhone,
                         String resetPhoneCode,
                         ShowRoomInfoViewModel showRoomInfoViewModel) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.website = website;
        this.address = address;
        this.imageUrl = imageUrl;
        this.coverUrl = coverUrl;
        this.firstName = firstName;
        this.birthYear = birthYear;
        this.tempPhone = tempPhone;
        this.resetPhoneCode = resetPhoneCode;
        this.showRoomInfoViewModel = showRoomInfoViewModel;
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

    public String getWebsite() {
        return website;
    }

    public String getAddress() {
        return address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public String getTempPhone() {
        return tempPhone;
    }

    public String getResetPhoneCode() {
        return resetPhoneCode;
    }

    public ShowRoomInfoViewModel getShowRoomInfoViewModel() {
        return showRoomInfoViewModel;
    }
}