package com.zamora.fastoreapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Sergio on 13/04/2017.
 */

public class ProductosListaActivity extends AppCompatActivity {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    public final static ArrayList<Producto> listaProductosGlobales = new ArrayList<>();
    private AdapterProductosCompra adapter;
    String idLista;
    public static String txtSpeechInput;
    public static String nombreLista;
    public static String nombreUser;
    ListaCompras listaCompras;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    final public static ArrayList<Producto> productos  = new ArrayList<>();
    public Context context;

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

        final DatabaseReference refHijoUsuarioP = database.getReference("Usuarios"+"/"+ nombreUser+"/Listas");
        refHijoUsuarioP.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productos.removeAll(productos);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getKey().equals(nombreLista)) {
                        for (DataSnapshot hijoList : snapshot.getChildren()) {
                            //Toast.makeText(getApplicationContext(),hijoList.getKey(),Toast.LENGTH_LONG).show();
                            if (hijoList.getKey().equals("Detalle")) {
                                for (DataSnapshot hijoD : hijoList.getChildren()) {
                                    Producto producto = hijoD.getValue(Producto.class);
                                    productos.add(producto);
                                }
                            }
                        }
                    }
                }
                //Toast.makeText(getApplicationContext(),"Cargando lista de productos",Toast.LENGTH_LONG).show();
                adapter.notifyDataSetChanged();
            }
//+nombreLista+"/Detalle"
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        final DatabaseReference refProductos = database.getReference("Productos");
        refProductos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaProductosGlobales.removeAll(listaProductosGlobales);
                for (DataSnapshot datosProd : dataSnapshot.getChildren()){
                    //Toast.makeText(getApplicationContext(),)
                    Producto producto = datosProd.getValue(Producto.class);
                    producto.setNombre(datosProd.getKey());
                    listaProductosGlobales.add(producto);
                }
                //Toast.makeText(getApplicationContext(),listaProductosGlobales.toString(),Toast.LENGTH_LONG).show();
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
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //int productoSeleccionado = productos.indexOf(parent.getAdapter().getItem(position));
                Producto productSelected = (Producto) parent.getAdapter().getItem(position);
                if (!productSelected.getInCart()) {
                    productSelected.setInCart(true);
                } else {
                    productSelected.setInCart(false);
                }
                DatabaseReference refProductoCar = database.getReference("Usuarios/"+nombreUser+"/Listas/"+nombreLista+"/Detalle/"+productSelected.getNombre());
                Map<String,Object> cambio = new HashMap<String, Object>();
                cambio.put("inCart",productSelected.getInCart());
                refProductoCar.updateChildren(cambio);
                adapter.notifyDataSetChanged();
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
                    txtSpeechInput = result.get(0);
                    String[] parse = txtSpeechInput.split(" ");
                    //Toast.makeText(this, result.get(0), Toast.LENGTH_LONG).show();
                    confirmSpeechText(result.get(0),parse);
                }
                break;
            }

        }
    }


    public AlertDialog confirmSpeechText(final String speechText,final String[] parse) {
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
                                nuevoProducto.setContext(getApplicationContext());
                                ArrayList<String> numerosN = new ArrayList<String>();
                                ArrayList<String> numerosL = new ArrayList<String>();
                                numerosL.add("uno");numerosL.add("dos");numerosL.add("tres");
                                numerosL.add("cuatro");numerosL.add("cinco");numerosL.add("seis");
                                numerosL.add("siete");numerosL.add("ocho");numerosL.add("nueve");
                                numerosL.add("dies");numerosL.add("once");numerosL.add("doce");
                                numerosL.add("trece");numerosL.add("catorce");numerosL.add("quince");
                                for(int i = 0; i< 51;i++){
                                    numerosN.add(String.valueOf(i));
                                }
                                if(parse.length == 1){
                                    nuevoProducto.setCantidad(1);
                                    for (int p = 0; p < listaProductosGlobales.size(); p++) {
                                        if (parse[0].equals(listaProductosGlobales.get(p).getNombre().toLowerCase())) {
                                            //Toast.makeText(getApplicationContext(), parse[0], Toast.LENGTH_SHORT).show();
                                            nuevoProducto.setNombre(listaProductosGlobales.get(p).getNombre());
                                            nuevoProducto.setPrecio(listaProductosGlobales.get(p).getPrecio());
                                        }
                                    }
                                }
                                else {
                                    for (int i = 0; i < parse.length; i++) {
                                        //Toast.makeText(getApplicationContext(),parse[i],Toast.LENGTH_SHORT).show();
                                        for (int n = 0; n < numerosN.size(); n++) {
                                            if (parse[i].equals(numerosN.get(n))) {
                                                //Toast.makeText(getApplicationContext(), parse[i], Toast.LENGTH_SHORT).show();
                                                nuevoProducto.setCantidad(n);
                                            }
                                        }
                                        for (int nL = 1; nL < numerosL.size(); nL++) {
                                            if (parse[i].equals(numerosL.get(nL))) {
                                                //Toast.makeText(getApplicationContext(), parse[i], Toast.LENGTH_SHORT).show();
                                                nuevoProducto.setCantidad(nL);
                                            }
                                        }
                                        for (int p = 0; p < listaProductosGlobales.size(); p++) {
                                            if (parse[i].equals(listaProductosGlobales.get(p).getNombre().toLowerCase())) {
                                                //Toast.makeText(getApplicationContext(), parse[i], Toast.LENGTH_SHORT).show();
                                                nuevoProducto.setNombre(listaProductosGlobales.get(p).getNombre());
                                                nuevoProducto.setPrecio(listaProductosGlobales.get(p).getPrecio());
                                            }
                                        }
                                    }
                                }
                                nuevoProducto.insertar(nuevoProducto,nombreLista,nombreUser);
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
    /*public void opcionesElemento(final Producto selectedPro, final int posicion) {
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
                        Toast.makeText(getApplicationContext(), "Eiminando Producto", Toast.LENGTH_SHORT).show();

                        adapter.notifyDataSetChanged();
                    }
                }
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }*/
}
