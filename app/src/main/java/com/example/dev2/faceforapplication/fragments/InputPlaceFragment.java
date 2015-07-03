package com.example.dev2.faceforapplication.fragments;

import android.app.Activity;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dev2.faceforapplication.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InputPlaceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InputPlaceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InputPlaceFragment extends Fragment {

    /**
     * The constant TAG.
     */
    public static final String TAG ="InputPlaceFragment";


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static InputPlaceFragment fragment;

    private static TextView textView;
    private ImageButton btCleane;

    private View inputPlaceFragment;

    /**
     * New instance.
     *
     * @return the input place fragment
     */
    public InputPlaceFragment newInstance() {
        if (fragment == null) {
            fragment = new InputPlaceFragment();
        }
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InputPlaceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InputPlaceFragment newInstance(String param1, String param2) {
        InputPlaceFragment fragment = new InputPlaceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Instantiates a new Input place fragment.
     */
    public InputPlaceFragment() {
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
        setRetainInstance(true);
        inputPlaceFragment = View.inflate(getActivity(), R.layout.fragment_input_place, null);
        // Inflate the layout for this fragment
        textView = (TextView) inputPlaceFragment.findViewById(R.id.textView);
        textView.setSingleLine();

        btCleane = (ImageButton) inputPlaceFragment.findViewById(R.id.imageButton);
        btCleane.setOnClickListener(listener);
        btCleane.setOnLongClickListener(longClick);
        return inputPlaceFragment;
    }

    /**
     * Sets text in to text view.
     *
     * @param text the text
     */
    public static void  setTextInToTextView(String text) {

        if (text.length() >= 10) {
            textView.setTextSize(48);
        }
        textView.setText(text);
    }

    /**
     * Gets text from text view.
     *
     * @return the text from text view
     */
    public static String getTextFromTextView() {
        return textView.getText().toString();
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
            String text = textView.getText().toString();
            switch (v.getId()) {
                case R.id.imageButton:
                    if (textView.getText() != null && text.length() > 0) {
                        int index = text.length() - 1;
                        String text2 = text;
                        text = text2.substring(0, index);
                        if (textView.getText().toString().length() < 11) {
                            textView.setTextSize(56);
                            textView.setSingleLine();
                        }
                    }
                    ButtonsFragment.tg.startTone(ToneGenerator.TONE_DTMF_S);
                    if (ButtonsFragment.tg !=null)  ButtonsFragment.tg.stopTone();
                    break;
            }
            textView.setText(text);
            if (ButtonsFragment.tg !=null)  ButtonsFragment.tg.stopTone();
        }
    };

    /**
     * The Long click.
     */
    View.OnLongClickListener longClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            switch (v.getId()) {
                case R.id.imageButton:
                    ButtonsFragment.tg.startTone(ToneGenerator.TONE_DTMF_S);
                        textView.setText("");
                    break;
            }
            if (ButtonsFragment.tg !=null)  ButtonsFragment.tg.stopTone();
            return true;
        }
    };

}
