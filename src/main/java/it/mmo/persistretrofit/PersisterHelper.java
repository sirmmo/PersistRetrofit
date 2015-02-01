package it.mmo.persistretrofit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import it.mmo.persistretrofit.generators.SqlGenerator;

/**
 * Created by MMo on 01/02/2015.
 */
public class PersisterHelper extends SQLiteOpenHelper {
    private final List<Class> classes;

    public PersisterHelper(Context context, String name, List<Class> classes) {
        super(context, name, null, 1);
        this.classes = classes;

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        for (Class c :classes)
            sqLiteDatabase.execSQL(SqlGenerator.create_table_query(c));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

        for (Class c :classes)
        {
            sqLiteDatabase.execSQL(SqlGenerator.drop_table_query(c));
            sqLiteDatabase.execSQL(SqlGenerator.create_table_query(c));
        }
    }
}
