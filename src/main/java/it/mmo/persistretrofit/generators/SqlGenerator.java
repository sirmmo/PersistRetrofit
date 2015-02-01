package it.mmo.persistretrofit.generators;

import android.content.ContentValues;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import it.mmo.persistretrofit.annotations.model.Identifier;
import it.mmo.persistretrofit.annotations.model.Relationship;

/**
 * Created by MMo on 01/02/2015.
 */
public class SqlGenerator {
    public static String delete_query(Class c) {
        String tablename = c.getName().toLowerCase() + "_table";

        return null;
    }

    public static String insert_query(Class c) {
        String tablename = c.getName().toLowerCase() + "_table";
        return null;
    }

    public static String update_query(Class c) {
        String tablename = c.getName().toLowerCase() + "_table";
        return null;
    }

    public static String create_table_query(Class c) {
        String tablename = c.getName().toLowerCase() + "_table";
        List<String> rels = new ArrayList<String>();
        for (Field f : c.getFields()) {
            if (f.isAnnotationPresent(Relationship.class))
                rels.add(f.getType().getName());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE " + tablename + " (");
        sb.append("_id INTEGER PRIMARY KEY, ");
        sb.append("_edited BOOLEAN DEFAULT 0, ");
        sb.append("_deleted BOOLEAN DEFAULT 0, ");
        sb.append("_created BOOLEAN DEFAULT 0, ");
        for (String r : rels) {
            sb.append(r + "_id INTEGER REFERENCES " + r + "_table ( _id ) ON DELETE CASCADE ON UPDATE NO ACTION,");
        }
        sb.append("_json TEXT NOT NULL");
        sb.append(");");

        return sb.toString();
    }

    public static String drop_table_query(Class c) {
        String tablename = c.getName().toLowerCase() + "_table";
        return "DROP TABLE IF EXISTS " + tablename;
    }

    public static String last_id_query(Class c) {
        String tablename = c.getName().toLowerCase() + "_table";
        return "SELECT _id from " + tablename + " ORDER BY _ID ASC LIMIT 1";
    }

    public static String select_single_query(Class c, String id) {
        String tablename = c.getName().toLowerCase() + "_table";
        return "SELECT _json FROM " + tablename + " WHERE _id = " + id;
    }

    public static String select_all_query(Class c) {
        String tablename = c.getName().toLowerCase() + "_table";
        return "SELECT _json FROM " + tablename;
    }

    public static List<ContentValues> store_query(Class aClass, Object to_store) {
        List<ContentValues> ret = new ArrayList<ContentValues>();

        Field ident = null;
        for(Field f:aClass.getFields()) if (f.isAnnotationPresent(Identifier.class)) ident = f;

        if (to_store instanceof List){
            List<Object> listret = (List<Object>) to_store;
            for (Object o : listret){
                ContentValues cv = new ContentValues();
                cv.put("_json", new Gson().toJson(o));

                try {
                    cv.put("_id", ident.get(o).toString());
                } catch (IllegalAccessException e) {
                    cv.put("_id", 1); //TODO: fix element
                }
                ret.add(cv);
            }
        }
        return ret;
    }

    public static String get_table(Class c) {
        return c.getName().toLowerCase() + "_table";
    }
}
