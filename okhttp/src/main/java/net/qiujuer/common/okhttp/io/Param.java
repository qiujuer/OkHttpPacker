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
package net.qiujuer.common.okhttp.io;


import java.io.File;

/**
 * Param
 */
public class Param extends StrParam {
    private boolean isFile;

    public Param(String key, File file) {
        super(key, file.getAbsolutePath());
        isFile = true;
    }

    public Param(IOParam param) {
        this(param.key, param.file);
    }

    public Param(StrParam strParam) {
        super(strParam.key, strParam.value);
    }

    public Param(String key, String value) {
        super(key, value);
    }

    public Param(String key, int value) {
        super(key, value);
    }

    public Param(String key, float value) {
        super(key, value);
    }

    public Param(String key, long value) {
        super(key, value);
    }

    public Param(String key, double value) {
        super(key, value);
    }

    public StrParam getStringParam() {
        return new StrParam(key, value);
    }

    public IOParam getFileParam() {
        return new IOParam(key, new File(value));
    }

    public boolean isFile() {
        return isFile;
    }
}
