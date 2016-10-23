/*
 * Copyright (C) 2014-2016 Qiujuer <qiujuer@live.cn>
 * WebSite http://www.qiujuer.net
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
package net.qiujuer.common.okhttp.cookie;

import android.content.Context;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * This is cookie manager by Okhttp3, implements {@link CookieJar}
 */
public class CookieManager implements CookieJar {
    private final CookieStore cookieStore;

    private CookieManager(PersistentCookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    public static CookieJar createBySharedPreferences(Context context) {
        return new CookieManager(new PersistentCookieStore(context));
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (cookies != null && cookies.size() > 0) {
            for (Cookie item : cookies) {
                cookieStore.add(url, item);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        return cookieStore.get(url);
    }

    public void removeAll() {
        cookieStore.removeAll();
    }

    public List<Cookie> getCookies() {
        return cookieStore.getCookies();
    }

    public interface CookieStore {
        /**
         * Adds one HTTP cookie to the store. This is called for every
         * incoming HTTP response.
         * <p>
         * <p>A cookie to store may or may not be associated with an URI. If it
         * is not associated with an URI, the cookie's domain and path attribute
         * will indicate where it comes from. If it is associated with an URI and
         * its domain and path attribute are not speicifed, given URI will indicate
         * where this cookie comes from.
         * <p>
         * <p>If a cookie corresponding to the given URI already exists,
         * then it is replaced with the new one.
         *
         * @param uri    the uri this cookie associated with.
         *               if <tt>null</tt>, this cookie will not be associated
         *               with an URI
         * @param cookie the cookie to store
         * @throws NullPointerException if <tt>cookie</tt> is <tt>null</tt>
         * @see #get
         */
        public void add(HttpUrl uri, Cookie cookie);


        /**
         * Retrieve cookies associated with given URI, or whose domain matches the
         * given URI. Only cookies that have not expired are returned.
         * This is called for every outgoing HTTP request.
         *
         * @return an immutable list of HttpCookie,
         * return empty list if no cookies match the given URI
         * @throws NullPointerException if <tt>uri</tt> is <tt>null</tt>
         * @see #add
         */
        public List<Cookie> get(HttpUrl uri);


        /**
         * Get all not-expired cookies in cookie store.
         *
         * @return an immutable list of http cookies;
         * return empty list if there's no http cookie in store
         */
        public List<Cookie> getCookies();


        /**
         * Get all URIs which identify the cookies in this cookie store.
         *
         * @return an immutable list of URIs;
         * return empty list if no cookie in this cookie store
         * is associated with an URI
         */
        public List<HttpUrl> getHttpUrls();


        /**
         * Remove a cookie from store.
         *
         * @param uri    the uri this cookie associated with.
         *               if <tt>null</tt>, the cookie to be removed is not associated
         *               with an URI when added; if not <tt>null</tt>, the cookie
         *               to be removed is associated with the given URI when added.
         * @param cookie the cookie to remove
         * @return <tt>true</tt> if this store contained the specified cookie
         * @throws NullPointerException if <tt>cookie</tt> is <tt>null</tt>
         */
        public boolean remove(HttpUrl uri, Cookie cookie);


        /**
         * Remove all cookies in this cookie store.
         *
         * @return <tt>true</tt> if this store changed as a result of the call
         */
        public boolean removeAll();
    }
}
