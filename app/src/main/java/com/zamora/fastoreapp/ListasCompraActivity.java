package com.zamora.fastoreapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

        final DatabaseReference refLista = database.getReference("Usuarios/"+ user[0]+"/Listas");

        ListView lv = (ListView) findViewById(R.id.listaCompras);
        adapter = new AdapterListasComprasUsuario(this, arregloListasCompra);
        lv.setAdapter(adapter);

        refLista.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arregloListasCompra.removeAll(arregloListasCompra);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //if (snapshot.getKey() != "Informacion") {

                    ListaCompras listaUser = snapshot.getValue(ListaCompras.class);
                    arregloListasCompra.add(listaUser);
                    //}
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        idUsuario = "10";
        //leerUsuario(idUsuario);
        cargarListas(lv);
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

    public void cargarListas(ListView lv){
        //arregloListasCompra = usuario.getListasCompras();
        //Toast.makeText(getApplicationContext(),arregloListasCompra.toString(),Toast.LENGTH_LONG).show();


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
                startActivity(intent);
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



}