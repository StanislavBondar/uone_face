package com.example.dev2.faceforapplication.otherActivity;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.example.dev2.faceforapplication.MainActivity;
import com.example.dev2.faceforapplication.R;
import com.example.dev2.faceforapplication.fragments.EndCallFragment;
import com.example.dev2.faceforapplication.fragments.IconFragment;


/**
 * The type Call activity.
 */
public class CallActivity extends FragmentActivity  implements
        EndCallFragment.OnFragmentInteractionListener,
        IconFragment.OnFragmentInteractionListener{

    private FragmentManager manager;
    private FragmentTransaction transaction;

    private EndCallFragment endCallFragment;
    private IconFragment iconFragment;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);


        manager = getFragmentManager();
        endCallFragment = new EndCallFragment().newInstance();
        iconFragment = new IconFragment().newInstance();

        if (savedInstanceState == null) {
        transaction = manager.beginTransaction();
        transaction.add(R.id.ll_body_other,iconFragment, IconFragment.TAG );
        transaction.add(R.id.ll_botton_other,endCallFragment, EndCallFragment.TAG );
        transaction.commit();
        }
    }

    /**
     * On click from other activity.
     *
     * @param view the view
     */
    public void onClickFromOtherActivity(View view) {
        transaction = manager.beginTransaction();
        Fragment endButtFragmentByTag = manager.findFragmentByTag(EndCallFragment.TAG);
        Fragment icFragmentByTag = manager.findFragmentByTag(IconFragment.TAG);
        switch (view.getId()) {
            case R.id.imBt_end_call:
                if (icFragmentByTag != null) {
                    onBackPressed();
                }
                break;
        }

        transaction.commit();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_call, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

}
