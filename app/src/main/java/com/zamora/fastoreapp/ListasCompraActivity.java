package com.zamora.fastoreapp;

import android.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zamora.fastoreapp.Adapters.AdapterListasComprasUsuario;
import com.zamora.fastoreapp.Clases.ListaCompras;
import com.zamora.fastoreapp.Clases.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zamora on 01/04/2017.
 */

public class ListasCompraActivity extends AppCompatActivity{

    public final static ArrayList<ListaCompras> arregloListasCompra = new ArrayList<>();

    private AdapterListasComprasUsuario adapter;
    private int listaSeleccionada;
    public static String fechaSeleccionada;

    private String nombre;
    private String email;
    private String imagen;
    private String idUsuario;

    private final int MY_PERMISSIONS_REQUEST_WRITE_CALENDAR = 0;

    public static String[] user;
    //String nombreUsuario = "fevig1994";

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refUSuarios = database.getReference("Usuarios");
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_compras);
        nombre = getIntent().getExtras().getString("nombre");
        email = getIntent().getExtras().getString("email");
        imagen = getIntent().getExtras().getString("image");
        idUsuario = getIntent().getExtras().getString("id");
        System.out.println("El id recibido fue " + idUsuario);
        //user = email.split("@");
        getSupportActionBar().setTitle("Mis listas de compra");
        getSupportActionBar().setSubtitle("¡Bienvenido!");

        //final DatabaseReference refLista = database.getReference("Usuarios/"+ user[0]);
        final DatabaseReference refLista = database.getReference("Usuarios/"+ idUsuario);
        refLista.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //arregloListasCompra.removeAll(arregloListasCompra);
                arregloListasCompra.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getKey().equals("Listas")){
                        //Toast.makeText(getApplicationContext(),snapshot.getKey(),Toast.LENGTH_LONG).show();

                        for(DataSnapshot datosListasCompras : snapshot.getChildren()){
                            //Toast.makeText(getApplicationContext(),datosListasCompras.getKey(),Toast.LENGTH_LONG).show();
                            ListaCompras listaUser = datosListasCompras.getValue(ListaCompras.class);
                            arregloListasCompra.add(listaUser);
                        }
                        //adapter.notifyDataSetChanged();
                    }
                    if(snapshot.getKey().equals("Listas Compartidas")){
                        for (DataSnapshot datosListasCompartidas : snapshot.getChildren()) {
                            for (DataSnapshot datosListasCompartidasN : datosListasCompartidas.getChildren()) {
                                String userCompartio = datosListasCompartidas.getKey();
                                ListaCompras nombreListaCompartio = datosListasCompartidasN.getValue(ListaCompras.class);
                                final DatabaseReference refUserComp = database.getReference("Usuarios/" + userCompartio + "/Listas/" + nombreListaCompartio.getNombre());
                                //Toast.makeText(getApplicationContext(),nombreListaCompartio.getNombre(), Toast.LENGTH_LONG).show();
                                //Toast.makeText(getApplicationContext(),nombreListaCompartio.getNombre(),Toast.LENGTH_LONG).show();
                                ListaCompras listaUserComp = datosListasCompartidasN.getValue(ListaCompras.class);
                                listaUserComp.setIdUsuario(userCompartio);
                                //Toast.makeText(getApplicationContext(),listaUserComp.getDetalle().toString(),Toast.LENGTH_LONG).show();
                                arregloListasCompra.add(listaUserComp);
                            }
                        }
                        //adapter.notifyDataSetChanged();
                    }
                }
                //Toast.makeText(getApplicationContext(),"Cargandos Listas de compras",Toast.LENGTH_LONG).show();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //leerUsuario(idUsuario);
        cargarListas();
    }

    @Override
    protected void onStart() {
        super.onStart();
        refUSuarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //DataSnapshot us = dataSnapshot.child(user[0]);
                DataSnapshot us = dataSnapshot.child(idUsuario);
                boolean x = us.exists();
                if(!x){
                    Map<String, Object> newUser = new HashMap<String, Object>();
                    //newUser.put("Informacion",new Usuario(nombre,email,user[0]));
                    //refUSuarios.child(user[0]).updateChildren(newUser);
                    newUser.put("Informacion",new Usuario(nombre,email,idUsuario));
                    refUSuarios.child(idUsuario).updateChildren(newUser);
                    //Toast.makeText(getApplicationContext(),"Insertando Usuario",Toast.LENGTH_LONG).show();
                }
                //Toast.makeText(getApplicationContext(),dataSnapshot.getKey(),Toast.LENGTH_LONG).show();
                //arregloListasCompra.removeAll(arregloListasCompra);
                /*for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ListaCompras listaUser = snapshot.getValue(ListaCompras.class);
                    Toast.makeText(getApplicationContext(),listaUser.getIdUsuario(),Toast.LENGTH_LONG).show();
                    arregloListasCompra.add(listaUser);
                }*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*public void leerUsuario(String userId) {
        this.usuario = new Usuario();
        this.usuario.leer(getApplicationContext(), userId);
    }*/

    public void destroy(){
        //finish();
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    public void cargarListas(){
        //arregloListasCompra = usuario.getListasCompras();
        //Toast.makeText(getApplicationContext(),arregloListasCompra.toString(),Toast.LENGTH_LONG).show();

        ListView lv = (ListView) findViewById(R.id.listaCompras);
        adapter = new AdapterListasComprasUsuario(this, arregloListasCompra, idUsuario);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listaSeleccionada = arregloListasCompra.indexOf(parent.getAdapter().getItem(position));
                //Toast.makeText(getApplicationContext(), String.valueOf(listaSeleccionada), Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), parent.getAdapter().getItem(position).toString(), Toast.LENGTH_LONG).show();

                ListaCompras selectedList = (ListaCompras) parent.getAdapter().getItem(position);
                //String idLista = (ListaCompras) parent.getAdapter().getItem(position);
                Intent intent = new Intent(getApplicationContext(), ProductosListaActivity.class);
                intent.putExtra("nombreLista",selectedList.getNombre());
                intent.putExtra("idLista", selectedList.getId());
                intent.putExtra("idUsuario",selectedList.getIdUsuario());
                startActivity(intent);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListaCompras selectedList = (ListaCompras) parent.getAdapter().getItem(position);
                opcionesElemento(selectedList);
                return true;
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        /*MenuItem item = menu.findItem(R.id.itemSearch);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Buscar...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemAdd:
                //Intent nuevaLista = new Intent(this, NuevaListaActivity.class);
                //nuevaLista.putExtra("cantListas", arregloListasCompra.size()+1);
                //startActivity(nuevaLista);

                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.WRITE_CALENDAR)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.WRITE_CALENDAR},
                            MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
                }
                else {
                    String ultimoIDLista = arregloListasCompra.get(arregloListasCompra.size() - 1).getId();
                    String substring = ultimoIDLista.substring(Math.max(ultimoIDLista.length() - 4, 0));
                    int ultimo = Integer.parseInt(substring);
                    NuevaListaDialog nla = new NuevaListaDialog(ListasCompraActivity.this, idUsuario, ultimo);
                    nla.show();
                }

                return true;
            case R.id.itemProfile:
                DialogProfile dpf = new DialogProfile(ListasCompraActivity.this,nombre,email,imagen);
                dpf.show();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /**
     * Opciones al hacer una pulsación larga en un elemento de la lista
     */
    public void opcionesElemento(final ListaCompras selectedList) {
        final CharSequence[] opciones = {"Compartir", "Eliminar"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Opciones");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if(item == 0){
                    compartirLista(selectedList);
                }
                else if (item == 1) {
                    Boolean wasRemoved = arregloListasCompra.remove(selectedList);
                    if (wasRemoved) {
                        String tipoLista = selectedList.getFechaCompra();
                        int pos = tipoLista.indexOf('@');
                        if(pos == -1){
                            //DatabaseReference refEliminar = database.getReference("Usuarios/"+user[0]+"/Listas/"+selectedList.getNombre());
                            DatabaseReference refEliminar = database.getReference("Usuarios/"+idUsuario+"/Listas/"+selectedList.getNombre());
                            refEliminar.removeValue();
                            //DatabaseReference refEliminar1 = database.getReference("Usuarios/"+user[0]+"/Listas/"+selectedList.getNombre());
                            //refEliminar1.removeValue();
                        }
                        else{
                            //DatabaseReference refEliminar1 = database.getReference("Usuarios/"+user[0]+"/Listas Compartidas/"+selectedList.getIdUsuario()+"/"+selectedList.getNombre());
                            DatabaseReference refEliminar1 = database.getReference("Usuarios/"+idUsuario+"/Listas Compartidas/"+selectedList.getIdUsuario()+"/"+selectedList.getNombre());
                            refEliminar1.removeValue();
                            //Toast.makeText(getApplicationContext(),tipoLista,Toast.LENGTH_LONG).show();
                        }


                        //Toast.makeText(getApplicationContext(), "Eliminando la lista de compras", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    }
                }
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void compartirLista(final ListaCompras listaCompartir){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText correo = new EditText(this);
        correo.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setTitle("Compartir Lista Compras");
        builder.setView(correo);
        builder.setPositiveButton("Compartir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String x = correo.getText().toString();
                final String cadenaM = x.toLowerCase();
                //Toast.makeText(getApplicationContext(),cadenaM,Toast.LENGTH_LONG).show();
                final DatabaseReference refU = database.getReference("Usuarios");

                refU.orderByChild("Informacion/email").equalTo(cadenaM).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        System.out.println("El key del email " + cadenaM + " es: " + dataSnapshot.getKey());
                        //final DatabaseReference refHijoUsuario = database.getReference("Usuarios/"+parse[0]+"/Listas Compartidas/"+user[0]);
                        final DatabaseReference refHijoUsuario = database.getReference("Usuarios/"+dataSnapshot.getKey()+"/Listas Compartidas/"+idUsuario);
                        Map<String,Object> hijoP = new HashMap<>();
                        Map<String,String> hijoLista = new HashMap<>();
                        hijoLista.put("nombre",listaCompartir.getNombre());
                        hijoLista.put("fechaCompra",listaCompartir.getFechaCompra());
                        hijoLista.put("id", listaCompartir.getId());
                        hijoLista.put("idUsuario", listaCompartir.getIdUsuario());
                        hijoP.put(listaCompartir.getNombre(),hijoLista);
                        refHijoUsuario.updateChildren(hijoP);
                        //Toast.makeText(getApplicationContext(),"Compartiendo lista de compras",Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    /*@Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_CALENDAR: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    String ultimoIDLista = arregloListasCompra.get(arregloListasCompra.size()-1).getId();
                    String substring = ultimoIDLista.substring(Math.max(ultimoIDLista.length() - 4, 0));
                    int ultimo = Integer.parseInt(substring);
                    NuevaListaDialog nla = new NuevaListaDialog(ListasCompraActivity.this, idUsuario, ultimo);
                    nla.show();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }*/

}
