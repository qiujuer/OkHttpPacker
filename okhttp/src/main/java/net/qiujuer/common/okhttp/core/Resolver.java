package net.qiujuer.common.okhttp.core;

import java.lang.reflect.Type;

/**
 * Created by qiujuer
 * on 15/12/25.
 */
public interface Resolver {

    Object analysis(String rsp, Type type);

    Object analysis(String rsp, Class<?> subclass);
}
