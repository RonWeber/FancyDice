package com.mwapp.ron.fancydice;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


public class DropSettings extends DialogFragment {

    public DropSettings() {
        // Required empty public constructor
    }

    public interface DropSettingsListener {
        public void acceptNewDropSettings(int dropLow, int dropHigh);
    }

    DropSettingsListener listener;

    public static DropSettings newInstance(String title, int currentDropLow, int currentDropHigh) {
        DropSettings fragment = new DropSettings();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("currentDropLow", currentDropLow);
        args.putInt("currentDropHigh", currentDropHigh);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drop_settings, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Drop Dice?");
        getDialog().setTitle(title);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View realView = inflater.inflate(R.layout.fragment_drop_settings, null);
        //TODO: Figure out what context we're in right now, given that we just instantiated a view.
        final EditText dropLowText = (EditText) realView.findViewById(R.id.dropLowText);
        final EditText dropHighText = (EditText) realView.findViewById(R.id.dropHighText);
        dropLowText.setText(String.valueOf(getArguments().getInt("currentDropLow", 0)));
        dropHighText.setText(String.valueOf(getArguments().getInt("currentDropHigh", 0)));

        builder.setView(realView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //We don't bother checking these conversions because it's a number editing field, and can't be invalid.
                        int dropLow = Integer.parseInt(dropLowText.getText().toString());
                        int dropHigh = Integer.parseInt(dropHighText.getText().toString());

                        listener.acceptNewDropSettings(dropLow, dropHigh);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DropSettings.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (DropSettingsListener) context;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
