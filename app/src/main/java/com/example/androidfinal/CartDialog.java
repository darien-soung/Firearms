package com.example.androidfinal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CartDialog extends AppCompatDialogFragment {
    private TextView mAmount;
    private Button mAdd, mMinus;
    private CartDialogListener listener;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_cart_dialog, null);

        builder.setView(view).setTitle("Amount to purchase")
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            })
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String amount = mAmount.getText().toString();

                listener.applyTexts(amount);
            }
        });



        mAmount = view.findViewById(R.id.ed_number);
        mAdd = view.findViewById(R.id.bt_add);
        mMinus = view.findViewById(R.id.bt_minus);

        setListeners();

        return builder.create();
    }

    private void setListeners()
    {
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = Integer.parseInt(mAmount.getText().toString());

                if(i<9)
                    i++;
                mAmount.setText(i + "");
            }
        });

        mMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = Integer.parseInt(mAmount.getText().toString());

                if(i>0)
                    i--;

                mAmount.setText(i + "");
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try { //prevent other activities other than GunDetails from activating this dialog
            listener = (CartDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement CartDialogListener ");
        }
    }

    public interface CartDialogListener
    {
        void applyTexts(String amount);
    }
}
