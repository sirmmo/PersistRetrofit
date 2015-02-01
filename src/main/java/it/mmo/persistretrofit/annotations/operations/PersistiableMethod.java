package it.mmo.persistretrofit.annotations.operations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by MMo on 01/02/2015.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PersistiableMethod {
    public Class<?> type();
    public MethodType methodType();
}
