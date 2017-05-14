package com.zamora.fastoreapp;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by Freddy on 2/5/2017.
 */

public class DialogProfile extends Dialog implements View.OnClickListener{

    private LinearLayout ProfSection;
    private Button SingOut;
    private TextView Name,Email;
    private ImageView ProfPic;
    private String Nombre;
    private String email;
    private String urImg;
    public Context context;

    public DialogProfile(Context context, String nombre, String email, String urimg) {
        super(context);
        this.context = context;
        this.Nombre = nombre;
        this.email = email;
        this.urImg = urimg;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_profile);
        ProfSection = (LinearLayout) findViewById(R.id.profSection);
        SingOut = (Button) findViewById(R.id.btnLogOut);
        Name = (TextView) findViewById(R.id.name);
        Email = (TextView) findViewById(R.id.email);
        ProfPic = (ImageView) findViewById(R.id.profPic);
        Name.setText(Nombre);
        Email.setText(email);

        if(urImg!= "") {
            Glide.with(context).load(urImg).into(ProfPic);
        } else {
            ProfPic.setImageResource(R.drawable.photo);
        }
        SingOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogOut:
                ListasCompraActivity main = new ListasCompraActivity();
                main.destroy();

            default:
                break;
        }
    }
}