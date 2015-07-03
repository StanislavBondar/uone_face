package com.example.dev2.faceforapplication.fragments;

import android.app.Activity;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dev2.faceforapplication.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ButtonsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ButtonsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ButtonsFragment extends Fragment {

    /**
     * The constant TAG.
     */
    public static final String TAG ="ButtonsFragment";


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static ButtonsFragment fragment;

    private View buttonsFrgment;

//    private ImageButton btCleane;
    private ImageButton btStar;
//    private ImageButton btZero;
    private ImageButton btPount;
//    public static  TextView textView;

    private String idButton = "";
    private int idIntButton;
    private ImageButton[] bt = new ImageButton[11];

    private String saveText;

    private String text;
    /**
     * The constant tg.
     */
    public static  ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, (int)(ToneGenerator.MAX_VOLUME * 1));;

//    :	tg.startTone(ToneGenerator.TONE_PROP_BEEP);

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ButtonsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ButtonsFragment newInstance(String param1, String param2) {
        ButtonsFragment fragment = new ButtonsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * New instance.
     *
     * @return the buttons fragment
     */
    public ButtonsFragment newInstance() {
        if (fragment == null) {
            fragment = new ButtonsFragment();
        }
        return fragment;
    }

    /**
     * Instantiates a new Buttons fragment.
     */
    public ButtonsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRetainInstance(true);
        buttonsFrgment = View.inflate(getActivity(), R.layout.fragment_buttons, null);

//        btCleane = (ImageButton) buttonsFrgment.findViewById(R.id.imageButton);
        btStar = (ImageButton) buttonsFrgment.findViewById(R.id.imBt_11);
//        btZero = (ImageButton) buttonsFrgment.findViewById(R.id.imBt_10);
        btPount = (ImageButton) buttonsFrgment.findViewById(R.id.imBt_12);
//        textView = (TextView) buttonsFrgment.findViewById(R.id.textView);


//        btCleane.setOnClickListener(listener);
        btStar.setOnClickListener(listener);
//        btZero.setOnClickListener(listener);
        btPount.setOnClickListener(listener);





        for (int j = 1; j < 11; j++) {
            idButton = "imBt_" + j;
            idIntButton = getResources()
                    .getIdentifier(idButton, "id", getActivity().getPackageName());
            bt[j] = (ImageButton) buttonsFrgment.findViewById(idIntButton);
//            bt[j].setId(j);
            ;
            int finalJ=0;
            if (j == 10) {
                finalJ = 0;
            } else {
                finalJ = j;
            }
            final String b = String.valueOf(finalJ);

            bt[j].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    textView.setText(textView.getText().toString()+ b);
                    text =InputPlaceFragment.getTextFromTextView();
                    saveText  = text + b;
                    InputPlaceFragment.setTextInToTextView(saveText);
                    tg.startTone(Integer.parseInt(b));
                    if (tg != null) tg.stopTone();
                }
            });
        }if (tg !=null)  tg.stopTone();



        return buttonsFrgment;
    }

    /**
     * On button pressed.
     *
     * @param uri the uri
     */
// TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        /**
         * On fragment interaction.
         *
         * @param uri the uri
         */
// TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    /**
     * The Listener.
     */
    View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.imBt_11:
                    text = InputPlaceFragment.getTextFromTextView();
                    saveText= text+ "*";
                    tg.startTone(ToneGenerator.TONE_DTMF_A);
                    break;
                case R.id.imBt_12:
                    text = InputPlaceFragment.getTextFromTextView();
                    saveText = text + "#";
                    tg.startTone(ToneGenerator.TONE_DTMF_P);
                    break;
                default:
                    break;
            }
            InputPlaceFragment.setTextInToTextView(saveText);
            if (tg !=null)  tg.stopTone();
        }
    };




}
