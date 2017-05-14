package com.zamora.fastoreapp.Clases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zamora.fastoreapp.Database.DatabaseContract;
import com.zamora.fastoreapp.Database.DatabaseHelper;

import java.util.ArrayList;

import static com.zamora.fastoreapp.ListasCompraActivity.user;


/**
 * Created by Zamora on 29/03/2017.
 */

public class Usuario {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String nombre;
    private String email;
    private String id;
    private ArrayList<ListaCompras> listasCompras;

    public Usuario() {
        this.nombre = null;
        this.email = null;
        this.id = null;
    }

    public Usuario(String nombre, String email, String id) {
        this.nombre = nombre;
        this.email = email;
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<ListaCompras> getListasCompras() {
        return listasCompras;
    }

    public void setListasCompras(ArrayList<ListaCompras> listasCompras) {
        this.listasCompras = listasCompras;
    }

    /**
     * Función que inserta un usuario en la base de datos
     */
    public long insertar(Context context) {
        DatabaseHelper DatabaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = DatabaseHelper.getWritableDatabase();

        // Crear un mapa de valores donde las columnas son las llaves
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DataBaseEntry._ID, getId());
        values.put(DatabaseContract.DataBaseEntry.COLUMN_NAME_NOMBRE, getNombre());
        values.put(DatabaseContract.DataBaseEntry.COLUMN_NAME_EMAIL, getEmail());

        // Insertar la nueva fila
        return db.insert(DatabaseContract.DataBaseEntry.TABLE_NAME_USUARIO, null, values);
    }


    /**
     * Leer una persona desde la base de datos
     */
    public void leer (Context context, String identificacion){
        DatabaseHelper DatabaseHelper = new DatabaseHelper(context);

        // Obtiene la base de datos en modo lectura
        SQLiteDatabase db = DatabaseHelper.getReadableDatabase();

        // Define cuales columnas quiere solicitar // en este caso todas las de la clase
        String[] projection = {
                DatabaseContract.DataBaseEntry._ID,
                DatabaseContract.DataBaseEntry.COLUMN_NAME_APELLIDO,
                DatabaseContract.DataBaseEntry.COLUMN_NAME_NOMBRE,
                DatabaseContract.DataBaseEntry.COLUMN_NAME_EMAIL
        };

        // Filtro para el WHERE
        String selection = DatabaseContract.DataBaseEntry._ID + " = ?";
        String[] selectionArgs = {identificacion};

        // Resultados en el cursor
        Cursor cursor = db.query(
                DatabaseContract.DataBaseEntry.TABLE_NAME_USUARIO, // tabla
                projection, // columnas
                selection, // where
                selectionArgs, // valores del where
                null, // agrupamiento
                null, // filtros por grupo
                null // orden
        );

        // recorrer los resultados y asignarlos a la clase // aca podria implementarse un ciclo si es necesario
        System.out.println(String.valueOf(cursor.getCount()));
        if(cursor.moveToFirst() && cursor.getCount() > 0) {
            this.setId(cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.DataBaseEntry._ID)));
            this.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.DataBaseEntry.COLUMN_NAME_NOMBRE)));
            this.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.DataBaseEntry.COLUMN_NAME_EMAIL)));
        }

        this.setListasCompras(leerListasUsuario(context, identificacion));
    }


    /**
     * Leer las listas de compras pertenecientes a un usuario
     */
    public ArrayList<ListaCompras> leerListasUsuario (Context context, String usuario){
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
        String selection = DatabaseContract.DataBaseEntry.COLUMN_NAME_ID_USUARIO + " = ?";
        String[] selectionArgs = {usuario};

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

        final ArrayList<ListaCompras> misListas = new ArrayList<>();

        /*System.out.println(String.valueOf(cursor.getCount()));
        if(cursor.moveToFirst()) {
            do {
                ListaCompras miLista = new ListaCompras();
                miLista.setId(cursor.getString(cursor.getColumnIndexOrThrow(
                        DatabaseContract.DataBaseEntry._ID)));
                miLista.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(
                        DatabaseContract.DataBaseEntry.COLUMN_NAME_NOMBRE)));
                miLista.setIdUsuario(cursor.getString(cursor.getColumnIndexOrThrow(
                        DatabaseContract.DataBaseEntry.COLUMN_NAME_ID_USUARIO)));
                miLista.setFechaCompra(cursor.getString(cursor.getColumnIndexOrThrow(
                        DatabaseContract.DataBaseEntry.COLUMN_NAME_FECHA_COMPRA)));
                miLista.setMontoTotal(cursor.getDouble(cursor.getColumnIndexOrThrow(
                        DatabaseContract.DataBaseEntry.COLUMN_NAME_MONTO_TOTAL)));
                misListas.add(miLista);
            } while (cursor.moveToNext());
        }*/
        return misListas;
    }


    /**
     * Actualizar una persona en la base de datos
     */
    public int actualizar(Context context) {
        DatabaseHelper DatabaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = DatabaseHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DataBaseEntry.COLUMN_NAME_NOMBRE, getNombre());
        values.put(DatabaseContract.DataBaseEntry.COLUMN_NAME_EMAIL, getEmail());

        // Criterio de actualizacion
        String selection = DatabaseContract.DataBaseEntry._ID + " LIKE ?";
        // Se detallan los argumentos
        String[] selectionArgs = {getId()};
        // Actualizar la base de datos
        return db.update(DatabaseContract.DataBaseEntry.TABLE_NAME_USUARIO, values, selection, selectionArgs);
    }
}
