package net.qiujuer.common.okhttp.impl;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;

import net.qiujuer.common.okhttp.core.Resolver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by qiujuer
 * on 15/12/25.
 */
public class GsonResolver implements Resolver {
    private Gson mGson;

    public GsonResolver() {
        mGson = new Gson();
    }

    public GsonResolver(Gson gson) {
        mGson = gson;
    }

    public Gson getGson() {
        return mGson;
    }

    public void setGson(Gson gson) {
        this.mGson = gson;
    }

    @Override
    public Object analysis(String rsp, Type type) {
        if (type == String.class)
            return rsp;
        return mGson.fromJson(rsp, type);
    }

    @Override
    public Object analysis(String rsp, Class<?> subclass) {
        if (subclass == String.class)
            return rsp;
        try {
            return analysis(rsp, getSuperclassTypeParameter(subclass));
        } catch (RuntimeException e) {
            return mGson.fromJson(rsp, subclass);
        }
    }

    Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }
}
