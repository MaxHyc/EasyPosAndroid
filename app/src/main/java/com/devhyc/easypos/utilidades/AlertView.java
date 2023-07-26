package com.devhyc.easypos.utilidades;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.core.content.ContextCompat;

import com.devhyc.easypos.R;

public class AlertView
{
    public static void showError(String title,String message, Context ctx)
    {
        mostrarAlerta(title, message, ctx,R.drawable.warningred);
    }

    public static void showAlert(String title, String message, Context ctx)
    {
        mostrarAlerta(title, message, ctx,R.drawable.atencion);
    }

    public static void showServerError(String title, String message, Context ctx)
    {
        mostrarAlerta(title, message, ctx, R.drawable.ic_borrar);
    }

    public static void showOk(String title, String message, Context ctx)
    {
        mostrarAlerta(title,message, ctx, R.drawable.exito);
    }

    public static void mostrarAlerta(String title, String message, Context ctx,int imagen)
    {
        //Create a builder
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(imagen);
        //add buttons and listener
        EmptyListener pl = new EmptyListener();
        builder.setPositiveButton("Ok", pl);
        //Create the dialog
        AlertDialog ad = builder.create();
        //show
        ad.show();
        ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ctx,
                R.color.white
        ));
        ad.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(ctx,
                R.color.green
        ));
    }
}

class EmptyListener implements DialogInterface.OnClickListener
{
    @Override
    public void onClick(DialogInterface dialog, int which)
    {
    }
}
