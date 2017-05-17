package com.zamora.fastoreapp.Clases;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.zamora.fastoreapp.ListasCompraActivity.user;

/**
 * Created by Zamora on 29/03/2017.
 */

public class ListaCompras {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refUSuarios = database.getReference("Usuarios");

    private String id;
    private String nombre;
    private String idUsuario;
    private String fechaCompra;
    private Double montoTotal;
    private static Context context;
    private ArrayList<Producto> detalle;
    private ArrayList<Usuario> usuariosPermitidos;

    public ListaCompras(){
        detalle = new ArrayList<>();
        usuariosPermitidos = new ArrayList<>();
    }

    public ListaCompras(String id, String nombre,String idUsuario, String fechaCompra, Double montoTotal) {
        this.id = id;
        this.nombre = nombre;
        this.idUsuario = idUsuario;
        this.fechaCompra = fechaCompra;
        this.montoTotal = montoTotal;
        detalle = new ArrayList<>();
        usuariosPermitidos = new ArrayList<>();
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        ListaCompras.context = context;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(String fechaCompra) {
       this.fechaCompra = fechaCompra;
    }

    public Double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(Double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<Producto> getDetalle() {
        return detalle;
    }

    public void setDetalle(ArrayList<Producto> detalle) {
        this.detalle = detalle;
    }

    public ArrayList<Usuario> getUsuariosPermitidos() {
        return usuariosPermitidos;
    }

    public void setUsuariosPermitidos(ArrayList<Usuario> usuariosPermitidos) {
        this.usuariosPermitidos = usuariosPermitidos;
    }

    @Override
    public String toString() {
        return "ListaCompras{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", idUsuario='" + idUsuario + '\'' +
                ", fechaCompra='" + fechaCompra + '\'' +
                ", montoTotal=" + montoTotal +
                '}';
    }

    /**
     * Función que inserta una lista de compras en la base de datos
     */
    public void insertar(final ListaCompras lista) {
        final DatabaseReference refHijoUsuarioL = database.getReference("Usuarios"+"/"+ user[0]+"/Listas");
        Map<String,Object> hijoLista = new HashMap<String,Object>();
        hijoLista.put(lista.getNombre(),lista);
        refHijoUsuarioL.updateChildren(hijoLista);
        Toast.makeText(context,"Insertando lista de compras",Toast.LENGTH_LONG).show();
        /*refHijoUsuarioL.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot us = dataSnapshot.child(lista.getNombre());
                boolean x = us.exists();
                if(x == false){

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/



        /*DatabaseHelper DatabaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = DatabaseHelper.getWritableDatabase();

        // Crear un mapa de valores donde las columnas son las llaves
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DataBaseEntry._ID, getId());
        values.put(DatabaseContract.DataBaseEntry.COLUMN_NAME_NOMBRE, getNombre());
        values.put(DatabaseContract.DataBaseEntry.COLUMN_NAME_ID_USUARIO, getIdUsuario());
        values.put(DatabaseContract.DataBaseEntry.COLUMN_NAME_FECHA_COMPRA, getFechaCompra());
        values.put(DatabaseContract.DataBaseEntry.COLUMN_NAME_MONTO_TOTAL, getMontoTotal());

        // Insertar la nueva fila
        return db.insert(DatabaseContract.DataBaseEntry.TABLE_NAME_LISTA_COMPRA, null, values);*/
    }


    /**
     * Leer una lista de compras desde la base de datos
     */
    /*public void leer (Context context, String identificacion){
        DatabaseHelper DatabaseHelper = new DatabaseHelper(context);

        // Obtiene la base de datos en modo lectura
        SQLiteDatabase db = DatabaseHelper.getReadableDatabase();

        // Define cuales columnas quiere solicitar // en este caso todas las de la clase
        String[] projection = {
                DatabaseContract.DataBaseEntry._ID,
                DatabaseContract.DataBaseEntry.COLUMN_NAME_NOMBRE,
                DatabaseContract.DataBaseEntry.COLUMN_NAME_ID_USUARIO,
                DatabaseContract.DataBaseEntry.COLUMN_NAME_FECHA_COMPRA,
                DatabaseContract.DataBaseEntry.COLUMN_NAME_MONTO_TOTAL,
        };

        // Filtro para el WHERE
        //String selection = DatabaseContract.DataBaseEntry._ID + " = ?";
        String selection = DatabaseContract.DataBaseEntry._ID + " = ?";
        String[] selectionArgs = {identificacion};

        // Resultados en el cursor
        Cursor cursor = db.query(
                DatabaseContract.DataBaseEntry.TABLE_NAME_LISTA_COMPRA, // tabla
                projection, // columnas
                selection, // where
                selectionArgs, // valores del where
                null, // agrupamiento
                null, // filtros por grupo
                null // orden
        );

        if(cursor.moveToFirst() && cursor.getCount() > 0) {
            this.setId(cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.DataBaseEntry._ID)));
            this.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.DataBaseEntry.COLUMN_NAME_NOMBRE)));
            this.setIdUsuario(cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.DataBaseEntry.COLUMN_NAME_ID_USUARIO)));
            this.setFechaCompra(cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.DataBaseEntry.COLUMN_NAME_FECHA_COMPRA)));
            this.setMontoTotal(cursor.getDouble(cursor.getColumnIndexOrThrow(
                    DatabaseContract.DataBaseEntry.COLUMN_NAME_MONTO_TOTAL)));
        }
        this.setDetalle(leerProductosCompra(context, identificacion));
        db.close();
    }*/


    /**
     * Leer los productos grabados en cada lista del usuario
     */
    /*public ArrayList<Producto> leerProductosCompra (Context context, final String listaID){
        final DatabaseHelper DatabaseHelper = new DatabaseHelper(context);
        //Create new querybuilder
        SQLiteQueryBuilder _QB = new SQLiteQueryBuilder();

        //Specify books table and add join to categories table (use full_id for joining categories table)
        _QB.setTables(DatabaseContract.DataBaseEntry.TABLE_NAME_DETALLE_LISTA +
                " INNER JOIN " + DatabaseContract.DataBaseEntry.TABLE_NAME_PRODUCTO + " ON " +
                DatabaseContract.DataBaseEntry.TABLE_NAME_DETALLE_LISTA + "." + DatabaseContract.DataBaseEntry.COLUMN_NAME_ID_PRODUCTO +
                " = " +
                DatabaseContract.DataBaseEntry.TABLE_NAME_PRODUCTO + "." + DatabaseContract.DataBaseEntry._ID);

        //Open database connection
        SQLiteDatabase db = DatabaseHelper.getReadableDatabase();

        // Define cuales columnas quiere solicitar // en este caso todas las de la clase
        String[] projection = {
                DatabaseContract.DataBaseEntry.TABLE_NAME_PRODUCTO + "." + DatabaseContract.DataBaseEntry._ID,
                DatabaseContract.DataBaseEntry.TABLE_NAME_PRODUCTO + "." + DatabaseContract.DataBaseEntry.COLUMN_NAME_NOMBRE,
                DatabaseContract.DataBaseEntry.TABLE_NAME_PRODUCTO + "." + DatabaseContract.DataBaseEntry.COLUMN_NAME_PRECIO,
                DatabaseContract.DataBaseEntry.TABLE_NAME_PRODUCTO + "." + DatabaseContract.DataBaseEntry.COLUMN_NAME_IMAGEN
        };

        // Filtro para el WHERE
        String selection = DatabaseContract.DataBaseEntry.TABLE_NAME_DETALLE_LISTA + "." + DatabaseContract.DataBaseEntry.COLUMN_NAME_ID_LISTA + " = ?";
        String[] selectionArgs = {listaID};

        //Get cursor
        Cursor cursor = _QB.query(db, projection, selection, selectionArgs, null, null, null);

        final ArrayList<Producto> misProductos = new ArrayList<>();

        /*if(cursor.moveToFirst()) {
            do {
                Producto miProducto = new Producto();
                miProducto.setId(cursor.getInt(cursor.getColumnIndexOrThrow(
                        DatabaseContract.DataBaseEntry._ID)));
                miProducto.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(
                        DatabaseContract.DataBaseEntry.COLUMN_NAME_NOMBRE)));
                miProducto.setPrecio(cursor.getDouble(cursor.getColumnIndexOrThrow(
                        DatabaseContract.DataBaseEntry.COLUMN_NAME_PRECIO)));
                miProducto.setImagen(cursor.getString(cursor.getColumnIndexOrThrow(
                        DatabaseContract.DataBaseEntry.COLUMN_NAME_IMAGEN)));
                misProductos.add(miProducto);
            } while (cursor.moveToNext());
        }
        db.close();
        return misProductos;
    }*/


    /**
     * Actualizar una lista de compras en la base de datos
     */
    /*public int actualizar(Context context) {
        DatabaseHelper DatabaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = DatabaseHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DataBaseEntry.COLUMN_NAME_NOMBRE, getNombre());
        values.put(DatabaseContract.DataBaseEntry.COLUMN_NAME_ID_USUARIO, getIdUsuario());
        values.put(DatabaseContract.DataBaseEntry.COLUMN_NAME_FECHA_COMPRA, getFechaCompra());
        values.put(DatabaseContract.DataBaseEntry.COLUMN_NAME_MONTO_TOTAL, getMontoTotal());

        // Criterio de actualizacion
        String selection = DatabaseContract.DataBaseEntry._ID + " LIKE ?";
        // Se detallan los argumentos
        String[] selectionArgs = {getId()};
        // Actualizar la base de datos
        return db.update(DatabaseContract.DataBaseEntry.TABLE_NAME_LISTA_COMPRA, values, selection, selectionArgs);
    }*/
}
