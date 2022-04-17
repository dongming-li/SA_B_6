package com.cs309.nerdsbattle.nerds_battle.stats;

/**
 * StatsItem class represents the stats that will be displayed to the user and obtained
 * from the server.
 *
 * @author Matthew Kelly
 * Created by Matthew Kelly on 11/6/2017.
 */
public class StatsItem {
    /**
     * Name of the stat.
     */
    public String name;

    /**
     * Value of the stat.
     */
    public String value;

    /**
     * Stats Item constructor takes the name and value of the stat.
     * @param name
     *    name of the stat.
     * @param value
     *    value of the stat.
     */
    public StatsItem(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
