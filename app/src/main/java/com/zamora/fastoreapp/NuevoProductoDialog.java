package com.zamora.fastoreapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zamora.fastoreapp.Clases.Producto;

import java.util.HashMap;
import java.util.Map;

import static com.zamora.fastoreapp.ProductosListaActivity.listaProductosGlobales;

/**
 * Created by Zamora on 01/05/2017.
 */

public class NuevoProductoDialog extends Dialog implements View.OnClickListener{
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String nombreLista, nombreUser;
    private EditText txtNombre, txtCantidad, txtPrecio;
    private Button btnAgregar;
    public Context context;


    public NuevoProductoDialog(Context context, String nombreLista, String nombreUser) {
        super(context);
        this.context = context;
        this.nombreUser = nombreUser;
        this.nombreLista = nombreLista;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_nuevo_producto);

        txtNombre = (EditText) findViewById(R.id.nombre_input);
        txtCantidad = (EditText) findViewById(R.id.cant_input);
        txtPrecio = (EditText) findViewById(R.id.precio_input);
        btnAgregar = (Button) findViewById(R.id.btnCrear);

        btnAgregar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnCrear:
                if(txtNombre.getText().toString().equals("")|| txtCantidad.getText().toString().equals("")){
                    Toast.makeText(getContext(),"Datos nulos",Toast.LENGTH_SHORT).show();
                }
                else {
                    Producto nuevoProducto = new Producto();
                /*String original = "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝßàáâãäåæçèéêëìíîïðñòóôõöøùúûüýÿ";
                // Cadena de caracteres ASCII que reemplazarán los originales.
                String ascii = "AAAAAAACEEEEIIIIDNOOOOOOUUUUYBaaaaaaaceeeeiiiionoooooouuuuyy";
                String output = txtNombre.getText().toString().toLowerCase();
                for (int i=0; i<original.length(); i++) {
                    // Reemplazamos los caracteres especiales.

                    output = output.replace(original.charAt(i), ascii.charAt(i));

                }//for i*/
                    nuevoProducto.setNombre(txtNombre.getText().toString());
                    nuevoProducto.setCantidad(Integer.parseInt(txtCantidad.getText().toString()));
                    nuevoProducto.setContext(context);
                    if (!txtPrecio.getText().toString().equals("")) {
                        nuevoProducto.setPrecio(Double.parseDouble(txtPrecio.getText().toString()));
                        int number = 0;
                        for (int p = 0; p < listaProductosGlobales.size(); p++) {
                            if (nuevoProducto.getNombre().equals(listaProductosGlobales.get(p).getNombre())) {
                                //Toast.makeText(getApplicationContext(), parse[i], Toast.LENGTH_SHORT).show();
                                number = 1;
                                final DatabaseReference refP = database.getReference("Productos");
                                Map<String,Object> nuevoPrecio = new HashMap<String, Object>();
                                Map<String,Double> x = new HashMap<String,Double>();
                                x.put("precio",Double.parseDouble(txtPrecio.getText().toString()));
                                nuevoPrecio.put(txtNombre.getText().toString(),x);
                                refP.updateChildren(nuevoPrecio);
                            }
                        }
                        if(number == 0){
                            final DatabaseReference refPe = database.getReference("Productos");
                            Map<String,Object> nuevoPrecio = new HashMap<String, Object>();
                            Map<String,Double> x = new HashMap<String,Double>();
                            x.put("precio",Double.parseDouble(txtPrecio.getText().toString()));
                            nuevoPrecio.put(txtNombre.getText().toString(),x);
                            refPe.updateChildren(nuevoPrecio);
                        }
                    }
                    else{
                        for (int p = 0; p < listaProductosGlobales.size(); p++) {
                            if (nuevoProducto.getNombre().equals(listaProductosGlobales.get(p).getNombre())) {
                                //Toast.makeText(getApplicationContext(), parse[i], Toast.LENGTH_SHORT).show();
                                nuevoProducto.setNombre(listaProductosGlobales.get(p).getNombre());
                                nuevoProducto.setPrecio(listaProductosGlobales.get(p).getPrecio());
                            }
                        }
                    }
                    //DatabaseReference refHijoUsuario = database.getReference("Usuarios"+"/"+ListasCompraActivity.user[0]);
                    //refProducto.push().setValue(nuevoProducto);
                    nuevoProducto.insertar(nuevoProducto, nombreLista, nombreUser);
                /*if (idRetorno != -1) {
                    Toast.makeText(context, "Se insertó " + nuevoProducto.toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error al insertar el producto", Toast.LENGTH_SHORT).show();
                }
                idRetorno = nuevoProducto.insertarDetalle(context, listaCompras.getId());
                if (idRetorno != -1) {
                    Toast.makeText(context, "Se insertó el detalle relacionado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error al insertar el detalle", Toast.LENGTH_SHORT).show();
                }*/
                /*ArrayList<Producto> listaProductos = listaCompras.getDetalle();
                listaProductos.add(nuevoProducto);
                listaCompras.setDetalle(listaProductos);*/
                }
                this.dismiss();
                break;


            default:
                break;
        }
    }

}
