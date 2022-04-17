package com.cs309.nerdsbattle.nerds_battle.battle;

/**
 * Battle item that is thrown by a player in a range attack.
 *
 * @author Matthew Kelly
 * Created by Matthew Kelly on 11/5/2017.
 */
public class BattleItem {

    /**
     * X Position of a item.
     */
    private double xPos;

    /**
     * Y Position of a item.
     */
    private double yPos;

    /**
     * PhotoID of a item.
     */
    private int photoId;

    /**
     * ID of a item.
     */
    private int id;

    /**
     * Battle Item constructor
     * @param xPos
     *  starting x position
     * @param yPos
     *  starting y position
     * @param photoId
     *  photoId
     * @param id
     * Item id
     */
    public BattleItem(double xPos, double yPos, int photoId, int id) {
        this.xPos = xPos;
        this.id = id;
        this.photoId = photoId;
        this.yPos = yPos;
    }


}
