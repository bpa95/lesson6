package ru.ifmo.md.lesson6;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

public class AddFeedDialogFragment extends DialogFragment {

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(String title, String url);
    }

    NoticeDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_add_feed, null))
                .setTitle(R.string.dialog_add_feed_title)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText titleEdit = (EditText) AddFeedDialogFragment.this.getDialog().findViewById(R.id.add_title);
                        EditText urlEdit = (EditText) AddFeedDialogFragment.this.getDialog().findViewById(R.id.add_url);
                        String title = titleEdit.getText().toString();
                        String url = urlEdit.getText().toString();
                        mListener.onDialogPositiveClick(title, url);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddFeedDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
