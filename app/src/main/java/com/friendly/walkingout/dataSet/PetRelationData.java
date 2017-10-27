package com.friendly.walkingout.dataSet;


public class PetRelationData {

    private int         index;
    private String      name;

    public PetRelationData(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%d. %s", index, name);
    }

}
