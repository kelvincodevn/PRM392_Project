package com.example.pcbuilderguideapp.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ThirdParty implements Serializable {
    @SerializedName("companyName")
    private String companyName;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
} 