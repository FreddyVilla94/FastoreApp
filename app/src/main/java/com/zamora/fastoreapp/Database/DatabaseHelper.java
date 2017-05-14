package com.zamora.fastoreapp.Database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Zamora on 17/03/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "fastore.db";

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version,
                          DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear la base de datos de la app
        db.execSQL(DatabaseContract.SQL_CREATE_USUARIO);
        db.execSQL(DatabaseContract.SQL_CREATE_LISTA_COMPRAS);
        db.execSQL(DatabaseContract.SQL_CREATE_PRODUCTO);
        db.execSQL(DatabaseContract.SQL_CREATE_DETALLE_LISTA);
        db.execSQL(DatabaseContract.SQL_CREATE_LISTAS_COMPARTIDAS);
        db.execSQL("insert into usuarios (" + DatabaseContract.DataBaseEntry._ID + ",email,nombre,apellido) values('10','k@gmail.com','Kevin','Zamora')");
        db.execSQL("insert into usuarios (" + DatabaseContract.DataBaseEntry._ID + ",email,nombre,apellido) values('11','e@gmail.com','Elsa','Arias')");
        db.execSQL("insert into listas_compra (" + DatabaseContract.DataBaseEntry._ID + ",nombre,id_usuario,fecha_compra,monto_total) values('1','Lista 1','10','2017-04-01',25.08)");
        db.execSQL("insert into listas_compra (" + DatabaseContract.DataBaseEntry._ID + ",nombre,id_usuario,fecha_compra,monto_total) values('2','Lista 2','10','2017-04-01',33.51)");
        db.execSQL("insert into listas_compra (" + DatabaseContract.DataBaseEntry._ID + ",nombre,id_usuario,fecha_compra,monto_total) values('3','Lista 3','11','2017-04-01',33.51)");
        db.execSQL("insert into productos (" + DatabaseContract.DataBaseEntry._ID + ",nombre,precio) values(1,'Arroz',1500.00)");
        db.execSQL("insert into detalles_lista (" + DatabaseContract.DataBaseEntry._ID + ",id_lista, id_producto) values(1,'2',1)");
        //db.execSQL("insert into detalles_lista (" + DatabaseContract.DataBaseEntry._ID + ",id_lista, id_producto) values('2','00100003','1')");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Administracion de actualizaciones
        db.execSQL(DatabaseContract.SQL_DELETE_USUARIO);
        db.execSQL(DatabaseContract.SQL_DELETE_LISTA_COMPRAS);
        db.execSQL(DatabaseContract.SQL_DELETE_PRODUCTO);
        db.execSQL(DatabaseContract.SQL_DELETE_DETALLE_LISTA);
        db.execSQL(DatabaseContract.SQL_DELETE_LISTAS_COMPARTIDAS);
        onCreate(db);
    }
}
