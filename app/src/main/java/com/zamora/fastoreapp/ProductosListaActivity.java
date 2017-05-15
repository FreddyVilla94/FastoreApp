package com.zamora.fastoreapp;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zamora.fastoreapp.Adapters.AdapterProductosCompra;
import com.zamora.fastoreapp.Clases.ListaCompras;
import com.zamora.fastoreapp.Clases.Producto;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Sergio on 13/04/2017.
 */

public class ProductosListaActivity extends AppCompatActivity {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private AdapterProductosCompra adapter;
    String idLista;
    String nombreLista;
    String nombreUser;
    ListaCompras listaCompras;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    final public static ArrayList<Producto> productos  = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_productos_compra);

        Intent intent = getIntent();
        idLista = intent.getStringExtra("idLista");
        nombreLista = intent.getStringExtra("nombreLista");
        nombreUser = intent.getStringExtra("idUsuario");
        //listaCompras = new ListaCompras();
        //listaCompras.leer(this, nombreLista);
        //String nombre = listaCompras.getNombre();

        final DatabaseReference refHijoUsuario = database.getReference("Usuarios"+"/"+ nombreUser+"/Listas/"+nombreLista+"/Detalle");
        refHijoUsuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productos.removeAll(productos);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Producto producto = snapshot.getValue(Producto.class);
                    productos.add(producto);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //productos = listaCompras.getDetalle();
        cargarListas();
        getSupportActionBar().setTitle(nombreLista);
    }

    public void cargarListas(){
        /*for (int i = 0; i < productos.size(); i++) {
            System.out.println(productos.get(i).toString());
        }*/
        ListView lv = (ListView) findViewById(R.id.productList);
        adapter = new AdapterProductosCompra(this, productos);
        lv.setAdapter(adapter);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Producto selectedPro = (Producto) parent.getAdapter().getItem(position);
                opcionesElemento(selectedPro,position);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_productos_lista, menu);
        return true;
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Intent i = getIntent();
        finish();
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemAdd:
                NuevoProductoDialog npd = new NuevoProductoDialog(ProductosListaActivity.this,nombreLista,nombreUser);
                npd.show();
                //onRestart();

                adapter.notifyDataSetChanged();
                return true;

            case R.id.itemAddByVoice:
                promptSpeechInput();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Habla ahora");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Lo sentimos, tu dispositivo no soporta esta función",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //txtSpeechInput.setText(result.get(0));
                    //Toast.makeText(this, result.get(0), Toast.LENGTH_LONG).show();
                    confirmSpeechText(result.get(0));
                }
                break;
            }

        }
    }


    public AlertDialog confirmSpeechText(final String speechText) {
        final String capText = speechText.substring(0, 1).toUpperCase() + speechText.substring(1);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Agregar")
                .setMessage(capText)
                .setPositiveButton("Sí",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Producto nuevoProducto = new Producto();
                                nuevoProducto.setNombre(capText);

                                nuevoProducto.insertar(nuevoProducto,nombreLista,nombreUser);
                                /*if (idRetorno != -1) {
                                    Toast.makeText(getApplicationContext(), "Se insertó " + nuevoProducto.toString(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error al insertar el producto", Toast.LENGTH_SHORT).show();
                                }
                                idRetorno = nuevoProducto.insertarDetalle(getApplicationContext(), idLista);
                                if (idRetorno != -1) {
                                    Toast.makeText(getApplicationContext(), "Se insertó el detalle relacionado", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error al insertar el detalle", Toast.LENGTH_SHORT).show();
                                }
                                //new Producto().leerRegistrosDetalle(this);*/
                                onRestart();
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //listener.onNegativeButtonClick();
                                dialog.cancel();
                                promptSpeechInput();
                            }
                        });

        return builder.show();
    }
    public void opcionesElemento(final Producto selectedPro, final int posicion) {
        final CharSequence[] opciones = {"Añadido al carrito", "Eliminar"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Opciones");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if(item == 0){

                }
                else if (item == 1) {
                    Boolean wasRemoved = productos.remove(selectedPro);
                    if (wasRemoved) {
                        DatabaseReference refEliminar = database.getReference("Usuarios/"+nombreUser+"/Listas/"+nombreLista+"/Detalle/"+selectedPro.getNombre());
                        refEliminar.removeValue();
                        //Toast.makeText(getApplicationContext(), "Estoy removiendo del adapter, no de firebase", Toast.LENGTH_SHORT).show();

                        adapter.notifyDataSetChanged();
                    }
                }
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
