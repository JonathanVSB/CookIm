package com.example.cookim.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class BBDDIngredients extends SQLiteOpenHelper {

    String instrSQL = "CREATE TABLE Ingrediente (PK_Id INTEGER PRIMARY KEY, Nombre TEXT)";

    static String nomBBDD = "Ingredientes";

    static int versio = 1;

    public BBDDIngredients(@Nullable Context context, @Nullable String name, @Nullable CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public BBDDIngredients(Context context) {
        super(context, nomBBDD, null, versio);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(this.instrSQL);
        //db.execSQL("INSERT INTO Contacto (Nombre) values('hola')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS Ingredientes");
        db.execSQL(this.instrSQL);
    }
}
