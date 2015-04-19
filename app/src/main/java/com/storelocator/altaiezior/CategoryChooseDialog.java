package com.storelocator.altaiezior;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by altaiezior on 19/4/15.
 */
public class CategoryChooseDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_aciton)
                .setItems(R.array.choose_action_list,
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                break;
                            case 1:
                                break;
                        }
                    }
                });
        return builder.create();
    }
}
