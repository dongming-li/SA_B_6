package com.cs309.nerdsbattle.nerds_battle.server_inter;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Handler that uses the updateInter Interface to communicate between the Main Thread and spawned threads
 * using the update() method.
 *
 * @param <T>
 *     Class that extends the updateInter Interface.
 *
 * @author Matthew Kelly
 */
public final class updateHandler<T extends updateInter> extends Handler {

    /**
     * Message Type that signals the Main Thread to update.
     */
    public static final int MESSAGE_DATA_RECEIVED = 1;

    /**
     * Reference to the input updateInter object.
     */
    private final WeakReference<T> mClassReference;

    /**
     * Creates a updateHandler with a WeakReference to the input updateInter object.
     *
     * @param instance
     *   object that extends the updateInter Interface.
     */
    public updateHandler(final T instance) {
        mClassReference = new WeakReference<T>(instance);
    }
    /**
     * Method that handles the receiving of MESSAGE_DATA_RECEIVED.
     *
     * @param msg
     *   Message that uses the MESSAGE_DATA_RECEIVED parameter.
     */
    @Override
    public void handleMessage(Message msg) {
        if (msg.what == MESSAGE_DATA_RECEIVED) {
            final T instance = mClassReference.get();

            //Check if the instance is valid.
            if (instance != null) {
                instance.update();
            }
        }
    }
}