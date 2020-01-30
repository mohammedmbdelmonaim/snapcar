package com.intcore.snapcar.ui.addcar;

public class CashedCar {

    private int contactOption;
    private int postType;
    private int isTracked;
    private int condition;
    private int manufacturingYear;
    private int importer;
    private int transmission;
    private int colorId;
    private String kmTo;
    private int warranty;
    private String agentName;
    private int mvpi;
    private String notes;
    private int modelId;
    private String priceBefore;
    private String priceAfter;
    private String engineCapacity;
    private String image;
    private String installmentFrom;
    private String installmentTo;
    private int exchange;
    private String longitude;
    private String latitude;
    private String examinationImage;
    private int categoryId;
    private int brandId;
    private String price;
    private int vehicleRegistration;
    private int priceType;

    public CashedCar(int contactOption,
                     int postType,
                     int isTracked,
                     int condition,
                     int manufacturingYear,
                     int importer,
                     int transmission,
                     int colorId,
                     String kmTo,
                     int warranty,
                     String agentName,
                     int mvpi,
                     String notes,
                     int modelId,
                     String priceBefore,
                     String priceAfter,
                     String engineCapacity,
                     String image,
                     String installmentFrom,
                     String installmentTo,
                     int exchange,
                     String longitude,
                     String latitude,
                     String examinationImage,
                     int categoryId,
                     int brandId
            , String price, int vehicleRegistration, int priceType) {

        this.contactOption = contactOption;
        this.postType = postType;
        this.isTracked = isTracked;
        this.condition = condition;
        this.manufacturingYear = manufacturingYear;
        this.importer = importer;
        this.transmission = transmission;
        this.colorId = colorId;
        this.kmTo = kmTo;
        this.warranty = warranty;
        this.agentName = agentName;
        this.mvpi = mvpi;
        this.notes = notes;
        this.modelId = modelId;
        this.priceBefore = priceBefore;
        this.priceAfter = priceAfter;
        this.engineCapacity = engineCapacity;
        this.image = image;
        this.installmentFrom = installmentFrom;
        this.installmentTo = installmentTo;
        this.exchange = exchange;
        this.longitude = longitude;
        this.latitude = latitude;
        this.examinationImage = examinationImage;
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.price = price;
        this.vehicleRegistration = vehicleRegistration;
        this.postType = postType;

    }

    public int getPriceType() {
        return priceType;
    }

    public int getVehicleRegistration() {
        return vehicleRegistration;
    }

    public int getContactOption() {
        return contactOption;
    }

    public int getPostType() {
        return postType;
    }

    public int getIsTracked() {
        return isTracked;
    }

    public int getCondition() {
        return condition;
    }

    public int getManufacturingYear() {
        return manufacturingYear;
    }

    public int getImporter() {
        return importer;
    }

    public int getTransmission() {
        return transmission;
    }

    public int getColorId() {
        return colorId;
    }

    public String getKmTo() {
        return kmTo;
    }

    public int getWarranty() {
        return warranty;
    }

    public String getAgentName() {
        return agentName;
    }

    public int getMvpi() {
        return mvpi;
    }

    public String getNotes() {
        return notes;
    }

    public int getModelId() {
        return modelId;
    }

    public String getPriceBefore() {
        return priceBefore;
    }

    public String getPriceAfter() {
        return priceAfter;
    }

    public String getEngineCapacity() {
        return engineCapacity;
    }

    public String getImage() {
        return image;
    }

    public String getInstallmentFrom() {
        return installmentFrom;
    }

    public String getInstallmentTo() {
        return installmentTo;
    }

    public int getExchange() {
        return exchange;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getExaminationImage() {
        return examinationImage;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getBrandId() {
        return brandId;
    }

    public String getPrice() {
        return price;
    }
}
