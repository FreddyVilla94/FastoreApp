package com.zamora.fastoreapp.Clases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zamora.fastoreapp.Database.DatabaseContract;
import com.zamora.fastoreapp.Database.DatabaseHelper;
import com.zamora.fastoreapp.ListasCompraActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zamora on 30/03/2017.
 */

public class Producto {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();


    private int id;
    private String nombre;
    private Double precio;
    private String imagen;

    public Producto(){}

    public Producto(int id, String nombre, Double precio, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.imagen = imagen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                '}';
    }

    /**
     * Función que inserta un producto en la base de datos
     */
    public void insertar(Producto context,String nombreLista) {
        //Producto nuevoProducto = new Producto(getId(),getNombre(),getPrecio(),"");
        DatabaseReference refProducto = database.getReference("Usuarios/"+ ListasCompraActivity.user[0]+"/Listas/"+nombreLista+"/Detalle");
        Map<String,Object> hijoProducto = new HashMap<String, Object>();
        hijoProducto.put(context.getNombre(),context);
        refProducto.updateChildren(hijoProducto);
        /*DatabaseHelper DatabaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = DatabaseHelper.getWritableDatabase();

        setId(leerUltimoProducto(context) + 1);
        // Crear un mapa de valores donde las columnas son las llaves
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DataBaseEntry._ID, getId());
        values.put(DatabaseContract.DataBaseEntry.COLUMN_NAME_NOMBRE, getNombre());
        values.put(DatabaseContract.DataBaseEntry.COLUMN_NAME_PRECIO, getPrecio());
        values.put(DatabaseContract.DataBaseEntry.COLUMN_NAME_IMAGEN, getImagen());
        System.out.println("Se va a insertar el " + toString());

        // Insertar la nueva fila
        return db.insert(DatabaseContract.DataBaseEntry.TABLE_NAME_PRODUCTO, null, values);*/
    }


    public long insertarDetalle(Context context, String idLista) {
        DatabaseHelper DatabaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = DatabaseHelper.getWritableDatabase();

        // Crear un mapa de valores donde las columnas son las llaves
        ContentValues values = new ContentValues();
        System.out.println("Insertando en detalle");
        int id = leerUltimoDetalle(context) + 1;
        System.out.println("El id del detalle es " + id);
        values.put(DatabaseContract.DataBaseEntry._ID, id);
        System.out.println("El id de la de la lista es " + idLista);
        values.put(DatabaseContract.DataBaseEntry.COLUMN_NAME_ID_LISTA, idLista);
        System.out.println("El id del producto es " + getId());
        values.put(DatabaseContract.DataBaseEntry.COLUMN_NAME_ID_PRODUCTO, getId());

        // Insertar la nueva fila
        long idRetorno = db.insert(DatabaseContract.DataBaseEntry.TABLE_NAME_DETALLE_LISTA, null, values);
        System.out.println("El id de retorno es " + idRetorno);
        return idRetorno;

    }


    /**
     * Leer un producto desde la base de datos
     */
    public void leer (Context context, String identificacion){
        DatabaseHelper DatabaseHelper = new DatabaseHelper(context);

        // Obtiene la base de datos en modo lectura
        SQLiteDatabase db = DatabaseHelper.getReadableDatabase();

        // Define cuales columnas quiere solicitar // en este caso todas las de la clase
        String[] projection = {
                DatabaseContract.DataBaseEntry._ID,
                DatabaseContract.DataBaseEntry.COLUMN_NAME_NOMBRE,
                DatabaseContract.DataBaseEntry.COLUMN_NAME_PRECIO,
                DatabaseContract.DataBaseEntry.COLUMN_NAME_IMAGEN,
        };

        // Filtro para el WHERE
        String selection = DatabaseContract.DataBaseEntry._ID + " = ?";
        String[] selectionArgs = {identificacion};

        // Resultados en el cursor
        Cursor cursor = db.query(
                DatabaseContract.DataBaseEntry.TABLE_NAME_PRODUCTO, // tabla
                projection, // columnas
                selection, // where
                selectionArgs, // valores del where
                null, // agrupamiento
                null, // filtros por grupo
                null // orden
        );

        // recorrer los resultados y asignarlos a la clase // aca podria implementarse un ciclo si es necesario
        if(cursor.moveToFirst() && cursor.getCount() > 0) {
            setId(cursor.getInt(cursor.getColumnIndexOrThrow(
                    DatabaseContract.DataBaseEntry._ID)));
            setNombre(cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.DataBaseEntry.COLUMN_NAME_NOMBRE)));
            setPrecio(cursor.getDouble(cursor.getColumnIndexOrThrow(
                    DatabaseContract.DataBaseEntry.COLUMN_NAME_PRECIO)));
            setImagen(cursor.getString(cursor.getColumnIndexOrThrow(
                    DatabaseContract.DataBaseEntry.COLUMN_NAME_IMAGEN)));
        }
        db.close();
    }


    /**
     * Leer el último registro de la tabla producto
     */
    public int leerUltimoProducto (Context context){
        DatabaseHelper DatabaseHelper = new DatabaseHelper(context);

        // Obtiene la base de datos en modo lectura
        SQLiteDatabase db = DatabaseHelper.getReadableDatabase();

        // Define cuales columnas quiere solicitar // en este caso todas las de la clase
        String[] projection = {
                DatabaseContract.DataBaseEntry._ID
        };

        // Orden
        String orderBy = DatabaseContract.DataBaseEntry._ID + " DESC";
        // Límite 1
        String limit = "1";

        // Resultados en el cursor
        Cursor cursor = db.query(
                DatabaseContract.DataBaseEntry.TABLE_NAME_PRODUCTO, // tabla
                projection, // columnas
                null, // where
                null, // valores del where
                null, // agrupamiento
                null, // filtros por grupo
                orderBy, // orden
                limit
        );

        int ultimoID = -1;
        // recorrer los resultados y asignarlos a la clase // aca podria implementarse un ciclo si es necesario
        if(cursor.moveToFirst() && cursor.getCount() > 0) {
            ultimoID = cursor.getInt(cursor.getColumnIndexOrThrow(
                    DatabaseContract.DataBaseEntry._ID));
        }
        db.close();
        System.out.println("El id de la ultima fila de la tabla productos es " + ultimoID);
        return ultimoID;
    }


    public int leerUltimoDetalle (Context context){
        DatabaseHelper DatabaseHelper = new DatabaseHelper(context);

        // Obtiene la base de datos en modo lectura
        SQLiteDatabase db = DatabaseHelper.getReadableDatabase();

        // Define cuales columnas quiere solicitar // en este caso todas las de la clase
        String[] projection = {
                DatabaseContract.DataBaseEntry._ID
        };

        // Orden
        String orderBy = DatabaseContract.DataBaseEntry._ID + " DESC";
        // Límite 1
        String limit = "1";

        // Resultados en el cursor
        Cursor cursor = db.query(
                DatabaseContract.DataBaseEntry.TABLE_NAME_DETALLE_LISTA, // tabla
                projection, // columnas
                null, // where
                null, // valores del where
                null, // agrupamiento
                null, // filtros por grupo
                orderBy, // orden
                limit
        );

        int ultimoID = -1;
        // recorrer los resultados y asignarlos a la clase // aca podria implementarse un ciclo si es necesario
        if(cursor.moveToFirst() && cursor.getCount() > 0) {
            ultimoID = cursor.getInt(cursor.getColumnIndexOrThrow(
                    DatabaseContract.DataBaseEntry._ID));
        }
        db.close();
        System.out.println("El id de la ultima fila de la tabla detalles es " + ultimoID);
        return ultimoID;
    }



    public void leerRegistrosDetalle (Context context){
        DatabaseHelper DatabaseHelper = new DatabaseHelper(context);

        // Obtiene la base de datos en modo lectura
        SQLiteDatabase db = DatabaseHelper.getReadableDatabase();

        // Define cuales columnas quiere solicitar // en este caso todas las de la clase
        String[] projection = {
                DatabaseContract.DataBaseEntry._ID,
                DatabaseContract.DataBaseEntry.COLUMN_NAME_ID_LISTA,
                DatabaseContract.DataBaseEntry.COLUMN_NAME_ID_PRODUCTO
        };

        // Resultados en el cursor
        Cursor cursor = db.query(
                DatabaseContract.DataBaseEntry.TABLE_NAME_DETALLE_LISTA, // tabla
                projection, // columnas
                null, // where
                null, // valores del where
                null, // agrupamiento
                null, // filtros por grupo
                null // orden
        );

        // recorrer los resultados y asignarlos a la clase // aca podria implementarse un ciclo si es necesario
        if(cursor.moveToFirst()) {
            do {
                System.out.println("Detalle { " + cursor.getInt(cursor.getColumnIndexOrThrow(
                        DatabaseContract.DataBaseEntry._ID)) + "\t" + cursor.getString(cursor.getColumnIndexOrThrow(
                        DatabaseContract.DataBaseEntry.COLUMN_NAME_ID_LISTA)) + "\t" + cursor.getInt(cursor.getColumnIndexOrThrow(
                        DatabaseContract.DataBaseEntry.COLUMN_NAME_ID_PRODUCTO)) + " }");
            } while (cursor.moveToNext());
        }
        db.close();
    }



    /**
     * Actualizar un prodcuto en la base de datos
     */
    public int actualizar(Context context) {
        DatabaseHelper DatabaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = DatabaseHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DataBaseEntry.COLUMN_NAME_NOMBRE, getNombre());
        values.put(DatabaseContract.DataBaseEntry.COLUMN_NAME_PRECIO, getPrecio());
        values.put(DatabaseContract.DataBaseEntry.COLUMN_NAME_IMAGEN, getImagen());

        // Criterio de actualizacion
        String selection = DatabaseContract.DataBaseEntry._ID + " LIKE ?";
        // Se detallan los argumentos
        String[] selectionArgs = { String.valueOf(getId()) };
        // Actualizar la base de datos
        return db.update(DatabaseContract.DataBaseEntry.TABLE_NAME_PRODUCTO, values, selection, selectionArgs);
    }
}
