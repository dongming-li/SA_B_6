/**
 * This is a singleton pattern Server Interaction class that will create
 * one RequestQueue object for the complete lifecycle of the application.
 */
package com.cs309.nerdsbattle.nerds_battle.server_inter;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Singleton pattern that contains the Volley RequestQueue used in the application.
 *
 * @author Matthew Kelly
 */
public class ServerInter {

    /**
     * ServerInter instance.
     */
    private static ServerInter serverInterInstance;

    /**
     * Volley Request Queue.
     */
    private RequestQueue requestQueue;

    /**
     * Context of the application this is used within.
     */
    private static Context context;

    /**
     * Private constructor that is only called by the getInstance method.
     * @param context
     *   context of the application.
     */
    private ServerInter(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    /**
     * Allows the user to get the single ServerInter object.
     *
     * @param context
     *   context of the application.
     * @return
     *   instance of the ServerInter.
     */
    public static synchronized ServerInter getInstance(Context context) {
        if (serverInterInstance == null) {
            serverInterInstance = new ServerInter(context);
        }
        return serverInterInstance;
    }

    /**
     * Creates the RequestQueue if there is not one already, otherwise it returns the
     * current one.
     * @return
     *   The request queue used within this application.
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    /**
     * Add a request to the request queue with the given tag.
     * @param req
     *   Request to add to the queue.
     * @param tag
     *   Tag for the request.
     * @param <T>
     *
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(tag);
        getRequestQueue().add(req);
    }

    /**
     * Cancels all requests in the request queue with the given tag.
     * @param tag
     *   tag of the request to be canceled.
     */
    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }
}
