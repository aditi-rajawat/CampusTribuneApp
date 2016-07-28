package com.campustribune.frontpage2;

/**
 * Created by sandyarathidas on 7/18/16.
 */
public class Data {

    private String itemId;
    private String itemTitle;
    private String itemContent;
    private String itemOwnerId;
    private String itemImageURL;
    private String itemCategory;
    private Boolean isItemAlert;
    private String itemType;


    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String getItemContent() {
        return itemContent;
    }

    public void setItemContent(String itemContent) {
        this.itemContent = itemContent;
    }

    public String getItemOwnerId() {
        return itemOwnerId;
    }

    public void setItemOwnerId(String itemOwnerId) {
        this.itemOwnerId = itemOwnerId;
    }

    public String getItemImageURL() {
        return itemImageURL;
    }

    public void setItemImageURL(String itemImageURL) {
        this.itemImageURL = itemImageURL;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public Boolean getIsItemAlert() {
        return isItemAlert;
    }

    public void setIsItemAlert(Boolean isItemAlert) {
        this.isItemAlert = isItemAlert;
    }
}