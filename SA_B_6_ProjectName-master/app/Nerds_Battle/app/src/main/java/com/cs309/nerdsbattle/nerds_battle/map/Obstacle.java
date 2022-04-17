package com.cs309.nerdsbattle.nerds_battle.map;

import com.cs309.nerdsbattle.nerds_battle.R;

/**
 * Obstacle class that represents all obstacles within this application
 */
public class Obstacle {

    private String obstacleName;
    private String type;
    private String description;
    private int photoID;
    private int height;
    private double width;
    private double length;
    private double xPos;
    private double yPos;
    private double rotation;

    /**
     * Obstacle's image to be displayed on GridView's
     */
    public static final int[] obstacleImages = {
            R.drawable.obstacle_lunchtable,
            R.drawable.obstacle_oldbookcase,
            R.drawable.obstacle_plasticchair,
            R.drawable.obstacle_woodendesk,
            R.drawable.obstacle_woodenstool
    };

    /**
     * Obstacle's image to be displayed on a map.
     */
    public static final int[] layoutobstacleImages = {
            R.drawable.layout_lunchtable,
            R.drawable.layout_oldbookcase,
            R.drawable.layout_plasticchair,
            R.drawable.layout_woodendesk,
            R.drawable.layout_woodenstool
    };

    /**
     * A deep copy constructor for an obstacle.
     * @param obstacle  an obstacle
     */
    // Copy constructor
    public Obstacle(Obstacle obstacle){
        this.obstacleName = obstacle.getObstacleName();
        this.type = obstacle.getType();
        this.photoID = obstacle.getPhotoID();
        this.length = obstacle.getLength();
        this.height = obstacle.getHeight();
        this.xPos = obstacle.getxPos();
        this.width = obstacle.getWidth();
        this.yPos = obstacle.getyPos();
        this.description = obstacle.getDescription();
        this.rotation = obstacle.getRotation();
    }

    /**
     * Constructs an obstacle without x and y positions on a map
     * @param obstacleName  Name of obstacle
     * @param type  Type of obstacle
     * @param description   Obstacle's description
     * @param photoID   Obstacle's photo ID
     * @param length    Length of obstacle
     * @param width     Width of obstacle
     * @param height    Height of obstacle
     */
    public Obstacle(String obstacleName, String type, String description, int photoID, double length, double width, int height){
        this.obstacleName = obstacleName;
        this.type = type;
        this.description = description;
        this.photoID = photoID;
        this.length = length;
        this.width = width;
        this.height = height;
    }

    /**
     * Constructs an obstacle with x and y positions on a map
     * @param obstacleName Name of obstacle
     * @param photoID   Obstacle's photo ID
     * @param length    Length of obstacle
     * @param width     Width of obstacle
     * @param xPos      Obstacle's x position on map
     * @param yPos      Obstacle's y position on map
     * @param rotation  Obstacle's rotation on map
     */
    public Obstacle(String obstacleName, int photoID, double length, double width, double xPos, double yPos, double rotation){
        this.obstacleName = obstacleName;
        this.photoID = photoID;
        this.length = length;
        this.width = width;
        this.xPos = xPos;
        this.yPos = yPos;
        this.rotation = rotation;
    }

    /**
     * Constructs a known obstacle with x and y positions on a map
     * @param obstacleName  Name of obstacle
     * @param xPos  Obstacle's x position on map
     * @param yPos  Obstacle's y position on map
     * @param rotation  Obstacle's rotation on map
     */
    public Obstacle(String obstacleName, double xPos, double yPos, double rotation){
        this.obstacleName = obstacleName;
        this.xPos = xPos;
        this.yPos = yPos;
        this.rotation = rotation;
    }

    /**
     * Get the width of obstacle
     * @return  Obstacle's width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Get the length of obstacle
     * @return  Obstacle's length
     */
    public double getLength() {
        return length;
    }

    /**
     * Get the height of obstacle
     * @return  Obstacle's height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the obstacle's name
     * @return  Name of obstacle
     */
    public String getObstacleName() {
        return obstacleName;
    }

    /**
     * Get the obstacle's type
     * @return  Type of obstacle
     */
    public String getType() {
        return type;
    }

    /**
     * Get the obstacle's description
     * @return  Obstacle's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the obstacle's photo ID.
     * @return  Obstacle's photo ID
     */
    public int getPhotoID() {
//        return 0;
        return photoID;
    }

    /**
     * Get the obstacle's x position
     * @return  Obstacle's x position
     */
    public double getxPos() {
        return xPos;
    }

    /**
     * Get the obstacle's y position
     * @return  Obstacle's y position
     */
    public double getyPos() {
        return yPos;
    }

    /**
     * Get the obstacle's rotation
     * @return  Obstacle's rotation
     */
    public double getRotation() {
        return rotation;
    }

    /**
     * Adjust obstacle's x position
     * @param x obstacle's x position
     */
    public void setxPos(double x){
        xPos = x;
    }

    /**
     * Adjust obstacle's y position
     * @param y obstacle's y position
     */
    public void setyPos(double y){
        yPos = y;
    }

}
