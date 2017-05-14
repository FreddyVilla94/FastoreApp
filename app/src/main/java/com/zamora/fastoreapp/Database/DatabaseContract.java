package com.zamora.fastoreapp.Database;

import android.provider.BaseColumns;

/**
 * Created by Zamora on 17/03/2017.
 */

public class DatabaseContract {
    // Implementa la interfaz BaseColumns para heredar campos estandar del marco de Android _ID

    public static class DataBaseEntry implements BaseColumns {

        // Clase Usuario
        public static final String TABLE_NAME_USUARIO = "usuarios";
        // private String identificacion; Utilizamos DataBaseEntry._ID de BaseColumns
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_NOMBRE = "nombre";
        public static final String COLUMN_NAME_APELLIDO = "apellido";
        public static final String COLUMN_NAME_FOTO_USUARIO = "foto_usuario";


        // Clase Lista Compra
        public static final String TABLE_NAME_LISTA_COMPRA = "listas_compra";
        // ID
        // COLUMN_NAME_NOMBRE -> se usa el que ya está en la tabla USUARIO
        public static final String COLUMN_NAME_ID_USUARIO = "id_usuario";
        public static final String COLUMN_NAME_FECHA_COMPRA = "fecha_compra";
        public static final String COLUMN_NAME_MONTO_TOTAL = "monto_total";

        // Clase Producto
        public static final String TABLE_NAME_PRODUCTO = "productos";
        // ID
        // Nombre
        public static final String COLUMN_NAME_PRECIO = "precio";
        public static final String COLUMN_NAME_IMAGEN = "imagen";

        // Clase Detalle Lista
        public static final String TABLE_NAME_DETALLE_LISTA = "detalles_lista";
        // ID
        public static final String COLUMN_NAME_ID_LISTA = "id_lista";
        public static final String COLUMN_NAME_ID_PRODUCTO = "id_producto";

        // Tabla Listas Compartidas
        public static final String TABLE_NAME_LISTAS_COMPARTIDAS = "listas_compartidas";
        // ID
        // ID lista
        // ID usuario
    }

// Construir las tablas de la base de datos
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";


    /**
     * Creación de tablas
     */

    public static final String SQL_CREATE_USUARIO =
            "CREATE TABLE " + DataBaseEntry.TABLE_NAME_USUARIO + " (" +
                    DataBaseEntry._ID + TEXT_TYPE + " PRIMARY KEY," +
                    DataBaseEntry.COLUMN_NAME_APELLIDO + TEXT_TYPE + COMMA_SEP +
                    DataBaseEntry.COLUMN_NAME_NOMBRE + TEXT_TYPE + COMMA_SEP +
                    DataBaseEntry.COLUMN_NAME_EMAIL + TEXT_TYPE + COMMA_SEP +
                    DataBaseEntry.COLUMN_NAME_FOTO_USUARIO + TEXT_TYPE + " )";

    public static final String SQL_DELETE_USUARIO =
            "DROP TABLE IF EXISTS " + DataBaseEntry.TABLE_NAME_USUARIO;



    public static final String SQL_CREATE_LISTA_COMPRAS =
            "CREATE TABLE " + DataBaseEntry.TABLE_NAME_LISTA_COMPRA + " (" +
                    DataBaseEntry._ID + TEXT_TYPE + " PRIMARY KEY," +
                    DataBaseEntry.COLUMN_NAME_NOMBRE + TEXT_TYPE + COMMA_SEP +
                    DataBaseEntry.COLUMN_NAME_ID_USUARIO + TEXT_TYPE + COMMA_SEP +
                    DataBaseEntry.COLUMN_NAME_FECHA_COMPRA + TEXT_TYPE + COMMA_SEP +
                    DataBaseEntry.COLUMN_NAME_MONTO_TOTAL + REAL_TYPE + COMMA_SEP +
                    " FOREIGN KEY (" + DataBaseEntry.COLUMN_NAME_ID_USUARIO +
                    ") REFERENCES " + DataBaseEntry.TABLE_NAME_USUARIO + "(" + DataBaseEntry._ID + ") )";

    public static final String SQL_DELETE_LISTA_COMPRAS =
            "DROP TABLE IF EXISTS " + DataBaseEntry.TABLE_NAME_LISTA_COMPRA;



    public static final String SQL_CREATE_PRODUCTO =
            "CREATE TABLE " + DataBaseEntry.TABLE_NAME_PRODUCTO + " (" +
                    DataBaseEntry._ID + INTEGER_TYPE + " PRIMARY KEY," +
                    DataBaseEntry.COLUMN_NAME_NOMBRE + TEXT_TYPE + COMMA_SEP +
                    DataBaseEntry.COLUMN_NAME_PRECIO + REAL_TYPE + COMMA_SEP +
                    DataBaseEntry.COLUMN_NAME_IMAGEN + TEXT_TYPE + " )";

    public static final String SQL_DELETE_PRODUCTO =
            "DROP TABLE IF EXISTS " + DataBaseEntry.TABLE_NAME_PRODUCTO;



    public static final String SQL_CREATE_DETALLE_LISTA =
            "CREATE TABLE " + DataBaseEntry.TABLE_NAME_DETALLE_LISTA + " (" +
                    DataBaseEntry._ID + INTEGER_TYPE + " PRIMARY KEY," +
                    DataBaseEntry.COLUMN_NAME_ID_LISTA + TEXT_TYPE + COMMA_SEP +
                    DataBaseEntry.COLUMN_NAME_ID_PRODUCTO + INTEGER_TYPE + COMMA_SEP +
                    " FOREIGN KEY (" + DataBaseEntry.COLUMN_NAME_ID_LISTA +
                    ") REFERENCES " + DataBaseEntry.TABLE_NAME_LISTA_COMPRA + "(" + DataBaseEntry._ID + ")" + COMMA_SEP +
                    " FOREIGN KEY (" + DataBaseEntry.COLUMN_NAME_ID_PRODUCTO +
                    ") REFERENCES " + DataBaseEntry.TABLE_NAME_PRODUCTO + "(" + DataBaseEntry._ID + ") )";

    public static final String SQL_DELETE_DETALLE_LISTA =
            "DROP TABLE IF EXISTS " + DataBaseEntry.TABLE_NAME_DETALLE_LISTA;



    public static final String SQL_CREATE_LISTAS_COMPARTIDAS =
            "CREATE TABLE " + DataBaseEntry.TABLE_NAME_LISTAS_COMPARTIDAS + " (" +
                    DataBaseEntry._ID + INTEGER_TYPE + " PRIMARY KEY," +
                    DataBaseEntry.COLUMN_NAME_ID_LISTA + TEXT_TYPE + COMMA_SEP +
                    DataBaseEntry.COLUMN_NAME_ID_USUARIO + TEXT_TYPE + COMMA_SEP +
                    " FOREIGN KEY (" + DataBaseEntry.COLUMN_NAME_ID_LISTA +
                    ") REFERENCES " + DataBaseEntry.TABLE_NAME_LISTA_COMPRA + "(" + DataBaseEntry._ID + ")" + COMMA_SEP +
                    " FOREIGN KEY (" + DataBaseEntry.COLUMN_NAME_ID_USUARIO +
                    ") REFERENCES " + DataBaseEntry.TABLE_NAME_USUARIO + "(" + DataBaseEntry._ID + ") )";

    public static final String SQL_DELETE_LISTAS_COMPARTIDAS =
            "DROP TABLE IF EXISTS " + DataBaseEntry.TABLE_NAME_LISTAS_COMPARTIDAS;
}
