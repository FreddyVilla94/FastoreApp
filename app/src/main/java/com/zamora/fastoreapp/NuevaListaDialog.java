package com.zamora.fastoreapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zamora.fastoreapp.Clases.ListaCompras;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//import static com.google.android.gms.internal.zzxo.hh;

/**
 * Created by Zamora on 02/04/2017.
 */

public class NuevaListaDialog extends Dialog implements View.OnClickListener{
    private String idUsuario;
    private int cantListas;
    private EditText txtFecha, txtHora, txtNombre;
    private Button btnCrearLista;
    private int dd, mm, yyyy, hora, minuto;
    public Context context;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refUSuarios = database.getReference("Usuarios");

    public NuevaListaDialog(Context context, String idUsuario, int cantListas) {
        super(context);
        this.context = context;
        this.idUsuario = idUsuario;
        this.cantListas = cantListas;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_nueva_lista);
        //getSupportActionBar().setTitle("Nueva lista");

        //cantListas = getIntent().getExtras().getInt("cantListas");

        //btnFecha = (Button) findViewById(R.id.btnFecha);
        txtFecha = (EditText) findViewById(R.id.fecha_input);
        txtHora = (EditText) findViewById(R.id.hora_input);
        txtNombre = (EditText) findViewById(R.id.nombre_input);
        btnCrearLista = (Button) findViewById(R.id.btnCrear);

        txtNombre.setText("Lista de compras " + cantListas);

        txtFecha.setOnClickListener(this);
        txtHora.setOnClickListener(this);
        btnCrearLista.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final Calendar calendar = Calendar.getInstance();
        switch (v.getId()) {
            case R.id.fecha_input:
                final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                try{
                    calendar.setTime(sdf.parse(txtFecha.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                dd = calendar.get(Calendar.DAY_OF_MONTH);
                mm = calendar.get(Calendar.MONTH);
                yyyy = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year, monthOfYear, dayOfMonth);
                        Date fecha = calendar.getTime();


                        //String date = dayOfMonth + "-" + (monthOfYear+1) +"-" + year;
                        txtFecha.setText(sdf.format(fecha));
                    }
                }, yyyy, mm, dd);
                datePickerDialog.show();
                break;

            case R.id.hora_input:
                hora = calendar.get(Calendar.HOUR_OF_DAY);
                minuto = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        int hora = hourOfDay % 12;
                        if (hora == 0)
                            hora = 12;
                        txtHora.setText(String.format(Locale.ENGLISH, "%02d:%02d %s", hora, minute,
                                hourOfDay < 12 ? "am" : "pm"));
                    }
                }, hora, minuto, false);
                timePickerDialog.show();
                break;

            case R.id.btnCrear:
                ListaCompras nuevaLista = new ListaCompras();
                nuevaLista.setId(String.format("%04d%04d", Integer.parseInt(idUsuario), cantListas));
                nuevaLista.setNombre(txtNombre.getText().toString());
                nuevaLista.setIdUsuario(ListasCompraActivity.user[0]);
                nuevaLista.setFechaCompra(txtFecha.getText().toString());

                nuevaLista.insertar(nuevaLista);
                Intent intent = new Intent(context, ProductosListaActivity.class);
                intent.putExtra("idLista", nuevaLista.getId());
                intent.putExtra("nombreLista",nuevaLista.getNombre());
                this.dismiss();
                //Toast.makeText(context, nuevaLista.toString(), Toast.LENGTH_SHORT).show();
                context.startActivity(intent);
                break;

            default:
                break;
        }

    }
}
