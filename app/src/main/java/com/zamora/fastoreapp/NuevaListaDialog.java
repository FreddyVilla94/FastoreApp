package com.zamora.fastoreapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zamora.fastoreapp.Clases.ListaCompras;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.R.attr.description;

//import static com.google.android.gms.internal.zzxo.hh;

/**
 * Created by Zamora on 02/04/2017.
 */

public class NuevaListaDialog extends Dialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private String idUsuario;
    private int cantListas;
    private EditText txtFecha, txtHora, txtNombre;
    private Button btnCrearLista;
    private Switch switchNotificaciones;
    private int dd, mm, yyyy, HH, MM;
    public Context context;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
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
        switchNotificaciones = (Switch) findViewById(R.id.switchNotification);
        btnCrearLista = (Button) findViewById(R.id.btnCrear);

        txtNombre.setText("Lista de compras " + (cantListas + 1));
        txtFecha.setOnClickListener(this);
        txtHora.setOnClickListener(this);
        btnCrearLista.setOnClickListener(this);
        switchNotificaciones.setOnCheckedChangeListener(this);
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
                HH = calendar.get(Calendar.HOUR_OF_DAY);
                MM = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        int hora = hourOfDay % 12;
                        if (hora == 0)
                            hora = 12;
                        txtHora.setText(String.format(Locale.ENGLISH, "%02d:%02d %s", hora, minute,
                                hourOfDay < 12 ? "am" : "pm"));
                    }
                }, HH, MM, false);
                timePickerDialog.show();
                break;

            case R.id.btnCrear:
                ListaCompras nuevaLista = new ListaCompras();
                //nuevaLista.setId(String.format("%04d%04d", Integer.parseInt(idUsuario), cantListas));
                cantListas+=1;
                String strCantListas = String.valueOf(cantListas);
                nuevaLista.setId(idUsuario + strCantListas);
                nuevaLista.setNombre(txtNombre.getText().toString());
                nuevaLista.setIdUsuario(idUsuario);
                nuevaLista.setFechaCompra(txtFecha.getText().toString());
                nuevaLista.setContext(context);
                nuevaLista.insertar(idUsuario);

                crearNotificacion(nuevaLista.getNombre());

                Intent intent = new Intent(context, ProductosListaActivity.class);
                intent.putExtra("idLista", nuevaLista.getId());
                intent.putExtra("nombreLista",nuevaLista.getNombre());
                intent.putExtra("idUsuario",nuevaLista.getIdUsuario());
                this.dismiss();
                //Toast.makeText(context, nuevaLista.toString(), Toast.LENGTH_SHORT).show();
                context.startActivity(intent);
                break;

            default:
                break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            txtFecha.setEnabled(true);
            txtHora.setEnabled(true);
        }
        else {
            txtFecha.setEnabled(false);
            txtHora.setEnabled(false);
        }
    }

    public void crearNotificacion(String title) {
        int dia, mes, anio, hora, minuto;

        String selectedDate = txtFecha.getText().toString();
        String[] separatedDate = selectedDate.split("/");
        dia = Integer.parseInt(separatedDate[0]);
        mes = Integer.parseInt(separatedDate[1]) - 1;
        anio = Integer.parseInt(separatedDate[2]);

        String selectedTime = txtHora.getText().toString();
        String strHora = selectedTime.substring(0,2);
        hora = Integer.parseInt(strHora);
        String strMinuto = selectedTime.substring(3,5);
        minuto = Integer.parseInt(strMinuto);
        String medioDia = selectedTime.substring(6);
        if (medioDia.equals("pm"))
            hora+=12;
        System.out.println("Hora:"+hora+" Minuto:"+minuto+" "+medioDia);

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(anio, mes, dia, hora, minuto);
        long startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        hora += 1;
        endTime.set(anio, mes, dia, hora, minuto);
        long endMillis = endTime.getTimeInMillis();

        int permissionCheck = ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.WRITE_CALENDAR);

        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, title + " está pendiente");
        values.put(CalendarContract.Events.DESCRIPTION, "Recordatorio de FasTore");
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());
        System.out.println("Se agregó el evento: " + eventID);
        Toast.makeText(context, "Se agregó el evento: " + eventID, Toast.LENGTH_SHORT).show();

        cr = context.getContentResolver();
        values = new ContentValues();
        values.put(CalendarContract.Reminders.MINUTES, 0);
        values.put(CalendarContract.Reminders.EVENT_ID, eventID);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
    }

}
