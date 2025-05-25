package com.example.pantrybuddy;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Recipe implements Parcelable {
    private String label;
    private String description;
    private List<String> ingredientLines;
    private String image;
    private String url;

    public Recipe(String label, String description, String url) {
        this.label = label;
        this.description = description;
        this.url = url;
    }

    public String getLabel() {
        return label;
    }

    public void setTitle(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getIngredientLines() {
        return ingredientLines;
    }

    public String getUrl() {
        return url;
    }

    public String getImage() {
        return image;
    }

    protected Recipe(Parcel in) {
        // Restore fields from the Parcel
        label = in.readString();
        description = in.readString();
        url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Write fields to the Parcel
        dest.writeString(label);
        dest.writeString(description);
        dest.writeString(url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

}
