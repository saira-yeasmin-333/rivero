package com.example.watcher24.model;

public class Area {
    private String area_name,population,erosionRate,loss,deadline,documentId,userId;

    public Area() {
    }

    public Area(String area_name, String population, String erosionRate, String loss, String deadline) {
        this.area_name = area_name;
        this.population = population;
        this.erosionRate = erosionRate;
        this.loss = loss;
        this.deadline = deadline;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

    public String getErosionRate() {
        return erosionRate;
    }

    public void setErosionRate(String erosionRate) {
        this.erosionRate = erosionRate;
    }

    public String getLoss() {
        return loss;
    }

    public void setLoss(String loss) {
        this.loss = loss;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return "Area{" +
                "area_name='" + area_name + '\'' +
                ", population='" + population + '\'' +
                ", erosionRate='" + erosionRate + '\'' +
                ", loss='" + loss + '\'' +
                ", deadline='" + deadline + '\'' +
                ", documentId='" + documentId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
