package com.intcore.snapcar.core.chat.model;

public class ContactDTO {

    private final String name;
    private final String number;

    public ContactDTO(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }
}
