package com.example.androidfinal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import org.w3c.dom.Comment;

public class CommentDialog extends AppCompatDialogFragment {
    private EditText mComment;
    private CommentDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_comment_dialog, null);

        builder.setView(view).setTitle("Add a comment")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String amount = mComment.getText().toString();

                        listener.applyComments(amount);
                    }
                });



        mComment = view.findViewById(R.id.ed_comment);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try { //prevent other activities other than GunDetails from activating this dialog
            listener = (CommentDialog.CommentDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement CommentDialogListener ");
        }
    }

    public interface CommentDialogListener
    {
        void applyComments(String comment);
    }
}
