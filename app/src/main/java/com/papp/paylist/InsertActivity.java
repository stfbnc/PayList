package com.papp.paylist;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class InsertActivity{// extends AppCompatActivity {

    public static final int TAG = 1;

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout., container, false);

        return v;
    }

    private void wchTempRelease() {
        Intent intent = new Intent(getActivity(), GramsDialogActivity.class);
        intent.putExtra(GramsDialog.TYPE, GramsDialog.WCH_TEMP_RELEASE);
        startActivityForResult(intent, GramsDialog.WCH_TEMP_RELEASE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GramsDialog.WCH_TEMP_RELEASE){
            if (resultCode == RESULT_OK) {
                String tmpRelPlace = data.getStringExtra("spinnerSelection");
                if(!tmpRelPlace.equals("-")) {
                    String tmpRelNote = data.getStringExtra("noteText");
                    String delta = data.getStringExtra("delta");

                    Bundle bndl = new Bundle();
                    bndl.putString(UALLOC, profilo.getWchs().get(uWch).getWchAllocUrno());
                    bndl.putString(RELPLACE, tmpRelPlace);
                    bndl.putString(RELREMA, tmpRelNote);
                    bndl.putString(RELDELTA, delta);
                    bndl.putString(Constants.PAYLOAD, "");
                    PostNetworkTask task = new PostNetworkTask(Constants.SEND_WCH_TMP_REL, bndl, mDispatcher);
                    task.execute();

                    getActivity().onBackPressed();
                }else{
                    Toast.makeText(getActivity(),"Non hai selezionato nessun luogo di rilascio temporaneo", Toast.LENGTH_LONG).show();
                }
            }
        }
    }*/

}
