package com.example.pcbuilderguideapp.models;

import com.google.gson.annotations.SerializedName;

public class ThirdParty {
    @SerializedName("companyName")
    private String companyName;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
} 