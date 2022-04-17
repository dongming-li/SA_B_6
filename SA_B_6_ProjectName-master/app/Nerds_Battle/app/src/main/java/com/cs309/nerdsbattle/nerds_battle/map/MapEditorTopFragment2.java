package com.cs309.nerdsbattle.nerds_battle.map;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cs309.nerdsbattle.nerds_battle.R;

/**
 * Fragment that contains the selected obstacle's detail
 * Created by Yee Jher.
 */
public class MapEditorTopFragment2 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(container!=null)
            container.removeAllViews();
        View view = inflater.inflate(R.layout.map_editor_top_fragment_2,container,false);
        new AsyncDataTask().execute();
        return view;
    }

    private class AsyncDataTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ((MapEditorOnFragmentFinishLoad) getActivity()).onFinish("From Top Fragment 2",true);
        }
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
}
