package com.example.dev2.faceforapplication.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.dev2.faceforapplication.R;
import com.example.dev2.faceforapplication.ViewPageActivity;
import com.example.dev2.faceforapplication.otherActivity.CallActivity;

import sipua.impl.DeviceImpl;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EndCallFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EndCallFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EndCallFragment extends Fragment {

    /**
     * The constant TAG.
     */
    public static final String TAG ="EndCallFragment";


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static EndCallFragment fragment;
    private View endCallFragment;
    private ImageButton imEndCall;

    /**
     * New instance.
     *
     * @return the end call fragment
     */
    public EndCallFragment newInstance() {
        if (fragment == null) {
            fragment = new EndCallFragment();
        }
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EndCallFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EndCallFragment newInstance(String param1, String param2) {
        EndCallFragment fragment = new EndCallFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Instantiates a new End call fragment.
     */
    public EndCallFragment() {
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
        endCallFragment = View.inflate(getActivity(), R.layout.fragment_end_call, null);
        // Inflate the layout for this fragment

        imEndCall = (ImageButton) endCallFragment.findViewById(R.id.imBt_end_call);
        imEndCall.setOnClickListener(listener);

        return endCallFragment;
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
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imBt_end_call:
                    DeviceImpl.GetInstance().Hangup();
                    getActivity().onBackPressed();
                    break;
            }
        }
    };



}
