package com.example.dev2.faceforapplication.otherActivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.dev2.faceforapplication.R;
import com.example.dev2.faceforapplication.fragments.ButtonsFragment;
import com.example.dev2.faceforapplication.fragments.CallButtonsFragment;
import com.example.dev2.faceforapplication.fragments.EndCallFragment;
import com.example.dev2.faceforapplication.fragments.IconFragment;
import com.example.dev2.faceforapplication.fragments.InputPlaceFragment;

public class WindowCallingActivity extends AppCompatActivity implements
        IconFragment.OnFragmentInteractionListener,
        CallButtonsFragment.OnFragmentInteractionListener,
        EndCallFragment.OnFragmentInteractionListener{

    private FragmentManager manager;
    private FragmentTransaction transaction;

    private IconFragment iconFragment;
    private CallButtonsFragment callButtonsFragment;
    private EndCallFragment endCallFragment;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_calling);

        manager = getFragmentManager();
        iconFragment = new IconFragment().newInstance();
        callButtonsFragment = new CallButtonsFragment().newInstance();
        endCallFragment = new EndCallFragment().newInstance();

        if (savedInstanceState == null) {
            transaction = manager.beginTransaction();
            transaction.add(R.id.ll_body_window_calling, iconFragment, IconFragment.TAG);
//            transaction.add(R.id.ll_call_up, callButtonsFragment, CallButtonsFragment.TAG);
            transaction.add(R.id.ll_call_cancel, endCallFragment, EndCallFragment.TAG);
            transaction.commit();
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void onClickButtonsCall(View view) {
        transaction = manager.beginTransaction();
        Fragment callButtFragmentByTag = manager.findFragmentByTag(CallButtonsFragment.TAG);
        Fragment endCallFragmentByTag = manager.findFragmentByTag(EndCallFragment.TAG);

        switch (view.getId()) {
            case R.id.imBt_call:
                if (endCallFragmentByTag != null && callButtFragmentByTag != null) {
                    intent = new Intent(WindowCallingActivity.this, CallActivity.class);
                    startActivity(intent);
                }
                break;
        }
        transaction.commit();
    }
    public void onClickFromOtherActivity(View view) {
        transaction = manager.beginTransaction();
        Fragment callButtFragmentByTag = manager.findFragmentByTag(CallButtonsFragment.TAG);
        Fragment endCallFragmentByTag = manager.findFragmentByTag(EndCallFragment.TAG);
        switch (view.getId()) {
            case R.id.imBt_end_call:
                if (endCallFragmentByTag != null && callButtFragmentByTag != null) {
                    onBackPressed();
                }
                break;
        }
        transaction.commit();
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_window_calling, menu);
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
