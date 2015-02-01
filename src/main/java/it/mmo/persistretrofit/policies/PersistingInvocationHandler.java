package it.mmo.persistretrofit.policies;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.mmo.persistretrofit.Persister;
import it.mmo.persistretrofit.annotations.operations.PersistiableMethod;
import it.mmo.persistretrofit.generators.SqlGenerator;
import retrofit.RestAdapter;

/**
 * Created by MMo on 01/02/2015.
 */
public class PersistingInvocationHandler implements InvocationHandler {

    private final Context c;

    private boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private RestAdapter ra;
    private HashMap<Method, Class> methods = new HashMap<Method, Class>();

    public PersistingInvocationHandler(List<Method> methods, RestAdapter ra, Context c){
        for (Method m: methods){
            this.methods.put(m, ((PersistiableMethod) m.getAnnotation(PersistiableMethod.class)).type());
        }
        this.ra = ra;
        this.c = c;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        if (methods.containsKey(method)){
            if (isConnected()){
                Object ret = method.invoke(o,objects);

                Persister.get().store(SqlGenerator.get_table(methods.get(method)), SqlGenerator.store_query(methods.get(method), ret));

                return ret;
            } else {
                List<Object> ret = new ArrayList<>();

                String selectQuery = SqlGenerator.select_all_query(methods.get(method));

                Cursor c = Persister.get().query(selectQuery);

                if (c.moveToFirst()) {
                    do {
                        String json = c.getString(c.getColumnIndex("_id"));
                        Object item = new Gson().fromJson(json, methods.get(method));
                        ret.add(item);
                    } while (c.moveToNext());
                }
                c.close();
                Persister.get().close();

                return ret;
            }
        } else {
            return method.invoke(o,objects);
        }
    }
}
