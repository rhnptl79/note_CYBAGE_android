package com.example.notesapp.data;

public class CategoryData {

    public CategoryData(int id,String name){
        this.cId=id;
        this.cName=name;
    }

    String cName;
    int cId;
    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public int getcId() {
        return cId;
    }

    public void setcId(int cId) {
        this.cId = cId;
    }

}
