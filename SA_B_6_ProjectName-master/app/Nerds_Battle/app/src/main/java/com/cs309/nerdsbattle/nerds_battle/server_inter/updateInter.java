package com.cs309.nerdsbattle.nerds_battle.server_inter;

/**
 * Interface used by the updateHandler to communicate to the Main Thread.
 *
 * @author Matthew Kelly
 */
public interface updateInter {

    /**
     * Method that handles the updating of the UI when information is obtained from the server.
     */
    void update();
}