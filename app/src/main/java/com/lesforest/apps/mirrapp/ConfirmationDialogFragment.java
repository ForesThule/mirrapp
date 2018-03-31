package com.lesforest.apps.mirrapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

public class ConfirmationDialogFragment extends DialogFragment {
 
    private static final String ARG_RESOURCES = "resources";
 
    /**
     * Creates a new instance of ConfirmationDialogFragment.
     *
     * @param resources The list of resources requested by PermissionRequest.
     * @return A new instance.
     */
    public static ConfirmationDialogFragment newInstance(String[] resources) {
        ConfirmationDialogFragment fragment = new ConfirmationDialogFragment();
        Bundle args = new Bundle();
        args.putStringArray(ARG_RESOURCES, resources);
        fragment.setArguments(args);
        return fragment;
    }
 
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[] resources = getArguments().getStringArray(ARG_RESOURCES);
        return new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.confirmation))
                .setNegativeButton(R.string.deny, (dialog, which) -> ((Listener) getParentFragment()).onConfirmation(false, resources))
                .setPositiveButton(R.string.allow, (dialog, which) -> ((Listener) getParentFragment()).onConfirmation(true, resources))
                .create();
    }
 
    /**
     * Callback for the user's response.
     */
    interface Listener {
        /**
         * Called when the PermissionRequest is allowed or denied by the user.
         *
         * @param allowed   True if the user allowed the request.
         * @param resources The resources to be granted.
         */
        void onConfirmation(boolean allowed, String[] resources);
    }
 
}