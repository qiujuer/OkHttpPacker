/*
 * Copyright (C) 2016 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
 * Created 1/1/2016
 * Changed 1/1/2016
 * Version 1.0.0
 * Author Qiujuer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.qiujuer.common.okhttp.impl;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;

import net.qiujuer.common.okhttp.core.HttpCore;
import net.qiujuer.common.okhttp.core.Resolver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * This Resolver by Gson
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
            if (HttpCore.DEBUG)
                e.printStackTrace();
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
