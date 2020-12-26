package com.jw.censo.constants;

public enum CardinalPoint {

    CARDINAL_POINT_SUR("SUR"),
    CARDINAL_POINT_ESTE("ESTE");

    private String name;

    CardinalPoint(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
