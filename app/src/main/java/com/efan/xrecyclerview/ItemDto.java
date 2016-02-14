package com.efan.xrecyclerview;

/**
 * Created by efan on 16-1-21.
 */
public class ItemDto {
    public int getDrawableId() {
        return drawableId;
    }

    public String getName() {
        return name;
    }

    public ItemDto(int drawableId, String name) {

        this.drawableId = drawableId;
        this.name = name;
    }

    private int drawableId;
    private String name;
}
