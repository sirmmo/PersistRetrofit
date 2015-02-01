package it.mmo.persistretrofit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import it.mmo.persistretrofit.annotations.model.PersistableClass;

/**
 * Created by MMo on 31/01/2015.
 */
public class Persister {
    private static Persister instance;

    private Persister(){}

    public static Persister get(){
        if (instance == null)
            instance = new Persister();
        return instance;
    }

    private static boolean initialized = false;
    private Context context;
    private ArrayList<File> directories = new ArrayList<File>();

    private PersisterHelper helper;
    public void Initialize(Context context, String namespace, String dbName) throws IOException, ClassNotFoundException {
        if (!this.initialized) {
            this.context = context;

            ClassLoader cld = context.getClassLoader();

            String path = namespace.replace('.', '/');
            Enumeration<URL> resources = cld.getResources(path);
            while (resources.hasMoreElements()) {
                directories.add(new File(URLDecoder.decode(resources.nextElement().getPath(), "UTF-8")));
            }

            ArrayList<Class> classes = new ArrayList<Class>();
            // For every directory identified capture all the .class files
            for (File directory : directories) {
                if (directory.exists()) {
                    // Get the list of the files contained in the package
                    String[] files = directory.list();
                    for (String file : files) {
                        // we are only interested in .class files
                        if (file.endsWith(".class")) {
                            // removes the .class extension
                            try {
                                classes.add(Class.forName(namespace + '.' + file.substring(0, file.length() - 6)));
                            } catch (NoClassDefFoundError e) {
                                // do nothing. this class hasn't been found by the loader, and we don't care.
                            }
                        }
                    }
                } else {
                    throw new ClassNotFoundException(namespace + " (" + directory.getPath() + ") does not appear to be a valid package");
                }
            }


            List<Class> persistableClasses = new ArrayList<Class>();
            for (Class c : classes){
                if (c.isAnnotationPresent(PersistableClass.class)){
                    persistableClasses.add(c);
                }
            }

            helper = new PersisterHelper(context, dbName, persistableClasses);

            initialized = true;
        }
    }


    public void close() {
        helper.close();
    }

    public Cursor query(String selectQuery) {
       return helper.getWritableDatabase().rawQuery(selectQuery, null);
    }

    public void store(String tablename, List<ContentValues> contentValues) {
        for (ContentValues cv: contentValues){
            helper.getWritableDatabase().insert(tablename, null, cv);
        }
    }
}
