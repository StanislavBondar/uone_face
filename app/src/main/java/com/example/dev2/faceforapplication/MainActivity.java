package com.example.dev2.faceforapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dev2.faceforapplication.fragments.ButtonsFragment;
import com.example.dev2.faceforapplication.fragments.CallButtonsFragment;
import com.example.dev2.faceforapplication.fragments.InputPlaceFragment;
import com.example.dev2.faceforapplication.otherActivity.CallActivity;
import com.example.dev2.faceforapplication.otherActivity.SettingActivity;
import com.example.dev2.faceforapplication.otherActivity.WindowCallingActivity;

import sipua.SipProfile;
import sipua.impl.DeviceImpl;
import sipua.impl.SipManager;

/**
 * The type Main activity.
 */
public class MainActivity extends ActionBarActivity implements
        ButtonsFragment.OnFragmentInteractionListener,
        CallButtonsFragment.OnFragmentInteractionListener,
        InputPlaceFragment.OnFragmentInteractionListener {


    private FragmentManager manager;
    private FragmentTransaction transaction;

    private ButtonsFragment buttonsFragment;
    private CallButtonsFragment callButtonsFragment;
    private InputPlaceFragment inputPlaceFragment;

    private CallActivity callActivity;
    private Intent intent;

    private ImageButton btCall;
    private ImageButton btClean;
    private TextView inputPlace;

    private View viewEditeText;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        viewEditeText = View.inflate(getApplicationContext(), R.layout.fragment_input_place, null);
        btCall = (ImageButton) viewEditeText.findViewById(R.id.imBt_call);
        btClean = (ImageButton) viewEditeText.findViewById(R.id.imageButton);

        manager = getSupportFragmentManager();
        buttonsFragment = new ButtonsFragment().newInstance();
        callButtonsFragment = new CallButtonsFragment().newInstance();
        inputPlaceFragment = new InputPlaceFragment().newInstance();




        if (savedInstanceState == null) {
            transaction = manager.beginTransaction();
            transaction.add(R.id.ll_head, inputPlaceFragment, InputPlaceFragment.TAG);
            transaction.add(R.id.ll_body, buttonsFragment, ButtonsFragment.TAG);
            transaction.add(R.id.ll_bottom, callButtonsFragment, CallButtonsFragment.TAG);
            transaction.commit();
        }
    }

    /**
     * On click buttons call.
     *
     * @param view the view
     */
    public void onClickButtonsCall(View view) {
        transaction = manager.beginTransaction();
        Fragment buttFragmentByTag = manager.findFragmentByTag(ButtonsFragment.TAG);
        Fragment callButtFragmentByTag = manager.findFragmentByTag(CallButtonsFragment.TAG);
        Fragment inputPlaceFragment = manager.findFragmentByTag(InputPlaceFragment.TAG);

        switch (view.getId()) {
            case R.id.imBt_call:
                if (buttFragmentByTag != null && callButtFragmentByTag != null) {
                    InputPlaceFragment.setTextInToTextView("");
                    intent = new Intent(MainActivity.this, CallActivity.class);
//                    intent = new Intent(MainActivity.this, WindowCallingActivity.class);
                    startActivity(intent);
                }
                break;
        }

        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.item_setting) {
            intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
