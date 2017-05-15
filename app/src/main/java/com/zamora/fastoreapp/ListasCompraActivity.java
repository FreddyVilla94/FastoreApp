package com.zamora.fastoreapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
    //publi cstatic ArrayList<ListaCompras> arregloListasCompra1;
    private static String idUsuario;
    private Usuario usuario;
    private AdapterListasComprasUsuario adapter;
    private int listaSeleccionada;
    public static String fechaSeleccionada;

    private String nombre;
    private String email;
    private String imagen;
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
        user = email.split("@");
        getSupportActionBar().setTitle("Mis listas de compra");
        getSupportActionBar().setSubtitle("Welcome");

        final DatabaseReference refLista = database.getReference("Usuarios/"+ user[0]);
        refLista.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arregloListasCompra.removeAll(arregloListasCompra);
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
                        for (DataSnapshot datosListasCompartidas : snapshot.getChildren()){
                            String userCompartio = datosListasCompartidas.getKey();
                            ListaCompras nombreListaCompartio = datosListasCompartidas.getValue(ListaCompras.class);
                            final DatabaseReference refUserComp = database.getReference("Usuarios/"+ userCompartio+"/Listas/"+nombreListaCompartio.getNombre());
                            //Toast.makeText(getApplicationContext(),refUserComp.getKey(),Toast.LENGTH_LONG).show();
                            //Toast.makeText(getApplicationContext(),nombreListaCompartio.getNombre(),Toast.LENGTH_LONG).show();
                            ListaCompras listaUserComp = datosListasCompartidas.getValue(ListaCompras.class);
                            listaUserComp.setIdUsuario(userCompartio);
                            //Toast.makeText(getApplicationContext(),listaUserComp.getDetalle().toString(),Toast.LENGTH_LONG).show();
                            arregloListasCompra.add(listaUserComp);
                        }

                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        idUsuario = "10";
        //leerUsuario(idUsuario);
        cargarListas();
    }

    @Override
    protected void onStart() {
        super.onStart();
        refUSuarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot us = dataSnapshot.child(user[0]);
                boolean x = us.exists();
                if(x == false){
                    Map<String, Object> newUser = new HashMap<String, Object>();
                    newUser.put("Informacion",new Usuario(nombre,email,user[0]));
                    refUSuarios.child(user[0]).updateChildren(newUser);
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

    public void leerUsuario(String userId) {
        this.usuario = new Usuario();
        this.usuario.leer(getApplicationContext(), userId);
    }

    public void destroy(){
        //finish();
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    public void cargarListas(){
        //arregloListasCompra = usuario.getListasCompras();
        //Toast.makeText(getApplicationContext(),arregloListasCompra.toString(),Toast.LENGTH_LONG).show();

        ListView lv = (ListView) findViewById(R.id.listaCompras);
        adapter = new AdapterListasComprasUsuario(this, arregloListasCompra);
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
                return false;
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

                NuevaListaDialog nla = new NuevaListaDialog(ListasCompraActivity.this, idUsuario, arregloListasCompra.size()+1);
                nla.show();
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
        final CharSequence[] opciones = {"Ver productos", "Configuración", "Compartir", "Eliminar"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Opciones");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if(item == 2){
                    compartirLista(selectedList);
                }
                else if (item == 3) {
                    Boolean wasRemoved = arregloListasCompra.remove(selectedList);
                    if (wasRemoved) {
                        DatabaseReference refEliminar = database.getReference("Usuarios/"+user[0]+"/Listas/"+selectedList.getNombre());
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
    public void compartirLista(final ListaCompras listaCompartir){
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        final EditText correo = new EditText(this);
        builder1.setTitle("Compartir Lista Compras");
        builder1.setView(correo);
        builder1.setPositiveButton("Compartir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String x = correo.getText().toString();
                String cadenaM = x.toLowerCase();
                Toast.makeText(getApplicationContext(),cadenaM,Toast.LENGTH_LONG).show();
                String[] parse = cadenaM.split("@");
                final DatabaseReference refHijoUsuario = database.getReference("Usuarios/"+parse[0]+"/Listas Compartidas/"+user[0]);
                Map<String,Object> hijoLista = new HashMap<String,Object>();
                hijoLista.put("nombre",listaCompartir.getNombre());
                hijoLista.put("fechaCompra",email);
                refHijoUsuario.updateChildren(hijoLista);
                /*refHijoUsuario.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DataSnapshot us = dataSnapshot.child(listaCompartir.getNombre());
                        boolean x = us.exists();
                        if(x == false){
                            Map<String,Object> hijoLista = new HashMap<String,Object>();
                            hijoLista.put(listaCompartir.getNombre(),listaCompartir.getId());
                            refHijoUsuario.updateChildren(hijoLista);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/
            }
        });
        AlertDialog alert = builder1.create();
        alert.show();
    }

}
