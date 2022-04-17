package com.cs309.nerdsbattle.nerds_battle.map;

/**
 * Interface used by MapEditor to check whether the fragments are finish loaded.
 */
public interface MapEditorOnFragmentFinishLoad {
    public void onFinish(String tag, boolean state);
}
