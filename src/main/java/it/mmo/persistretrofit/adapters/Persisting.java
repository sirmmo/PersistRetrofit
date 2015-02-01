package it.mmo.persistretrofit.adapters;

import android.content.Context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import it.mmo.persistretrofit.annotations.operations.PersistiableMethod;
import it.mmo.persistretrofit.policies.PersistingInvocationHandler;
import retrofit.RestAdapter;

/**
 * Created by MMo on 31/01/2015.
 */
public class Persisting {
    private final Context c;
    private RestAdapter ra;

    public Persisting(RestAdapter ra, Context c){
        this.ra = ra;
        this.c = c;
    }

    public <T> T create(Class<T> service){
        T t = ra.create(service);


        List<Method> ms = new ArrayList<Method>();
        for (Method m : service.getMethods()){
            if (m.isAnnotationPresent(PersistiableMethod.class)){
                ms.add(m);
            }
        }

        InvocationHandler ih = new PersistingInvocationHandler(ms, ra, c);

        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, ih);

    }



}
