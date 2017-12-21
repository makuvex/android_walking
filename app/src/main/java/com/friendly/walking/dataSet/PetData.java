package com.friendly.walking.dataSet;


public class PetData {

    public int index = -1;

    public String mem_email = "";
    public String petName = "";
    public boolean petGender = false;
    public String birthDay = "";
    public String petSpecies = "";
    public String petRelation = "";
    //public String petProfile = "";

    public PetData() {}

    public PetData(int index, String species) {
        this.index = index;
        this.petSpecies = species;
    }

    public String getSpecies() {
        return petSpecies;
    }

    @Override
    public String toString() {
        return String.format("%d. %s", index, petSpecies);
    }

}
