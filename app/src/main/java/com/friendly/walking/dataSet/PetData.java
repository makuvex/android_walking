package com.friendly.walking.dataSet;


public class PetData {

    private int         index;
    private String      species;

    public PetData(int index, String species) {
        this.index = index;
        this.species = species;
    }

    public int getIndex() {
        return index;
    }

    public String getSpecies() {
        return species;
    }

    @Override
    public String toString() {
        return String.format("%d. %s", index, species);
    }

}
