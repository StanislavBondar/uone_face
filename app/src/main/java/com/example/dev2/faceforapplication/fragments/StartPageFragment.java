package com.example.dev2.faceforapplication.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dev2.faceforapplication.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StartPageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StartPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartPageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private FragmentManager manager;
    private FragmentTransaction transaction;
    private View viewStartPageFragment;

    private ButtonsFragment buttonsFragment;
    private CallButtonsFragment callButtonsFragment;
    private InputPlaceFragment inputPlaceFragment;

    private OnFragmentInteractionListener mListener;

    private static StartPageFragment fragment;


    public StartPageFragment newInstance() {
        if (fragment == null) {
            fragment = new StartPageFragment();
        }
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StartPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StartPageFragment newInstance(String param1, String param2) {
        StartPageFragment fragment = new StartPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public StartPageFragment() {
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
        viewStartPageFragment = View.inflate(getActivity(), R.layout.fragment_start_page, null);

        manager = getFragmentManager();
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

        return viewStartPageFragment;
    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        if (!transaction.isEmpty()) {
//            transaction = manager.beginTransaction();
//            transaction.remove(inputPlaceFragment);
//            transaction.remove(buttonsFragment);
//            transaction.remove(callButtonsFragment);
//            transaction.commit();
////            transaction.replace(R.id.ll_head, inputPlaceFragment, InputPlaceFragment.TAG);
////            transaction.replace(R.id.ll_body, buttonsFragment, ButtonsFragment.TAG);
////            transaction.replace(R.id.ll_bottom, callButtonsFragment, CallButtonsFragment.TAG);
//
//        }
//    }

//    @Override
//    public void onStop() {
//        super.onStop();
//
//        if (!transaction.isEmpty()) {
//            transaction = manager.beginTransaction();
//            transaction.remove(inputPlaceFragment);
//            transaction.remove(buttonsFragment);
//            transaction.remove(callButtonsFragment);
//            transaction.commit();
////            transaction.replace(R.id.ll_head, inputPlaceFragment, InputPlaceFragment.TAG);
////            transaction.replace(R.id.ll_body, buttonsFragment, ButtonsFragment.TAG);
////            transaction.replace(R.id.ll_bottom, callButtonsFragment, CallButtonsFragment.TAG);
//
//        }
//    }

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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
