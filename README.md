# BaseJson
基于 org.json 的改进型 Json 解析库，快速解析 Json 为 Map 或 List 对象。

<a href="https://github.com/kongzue/BaseJson/">
<img src="https://img.shields.io/badge/BaseJson-1.0.7-green.svg" alt="BaseOkHttp">
</a>
<a href="https://github.com/kongzue/BaseJson/releases">
<img src="https://img.shields.io/github/v/release/kongzue/BaseJson?color=green" alt="Maven">
</a> 
<a href="https://jitpack.io/#kongzue/BaseJson">
<img src="https://jitpack.io/v/kongzue/BaseJson.svg" alt="Jitpack.io">
</a> 
<a href="http://www.apache.org/licenses/LICENSE-2.0">
<img src="https://img.shields.io/badge/License-Apache%202.0-red.svg" alt="License">
</a>
<a href="http://www.kongzue.com">
<img src="https://img.shields.io/badge/Homepage-Kongzue.com-brightgreen.svg" alt="Homepage">
</a>

## 简介
- 将 Json 转换为 Map 或 List 的子类，可直接用于 Adapter，省去编写 Bean 的过程。
- JsonMap 也可轻松与 JavaBean 互相转换。
- 高容错设计，使用自定义 get 方法，无论如何绝不会出现空指针，哪怕类型错误，你也无须担心空指针异常；
- 无缝衔接 Map 和 List，可随意通过 Map 或 List 直接转换为 Json 对象；
- 借助 Map 和 List 的优势，`.isEmpty()`判空，`KeySet()`遍历、`Collections.sort()`排序等操作帮助你轻松完成解析。

## Maven仓库或Gradle的引用方式

### jitPack

在项目根目录的 build.gradle(Project) 添加:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

#### 最新版本：

<a href="https://jitpack.io/#kongzue/BaseJson">
<img src="https://jitpack.io/v/kongzue/BaseJson.svg" alt="Jitpack.io">
</a> 

在 build.gradle(app) 添加:
```
dependencies {
        implementation 'com.github.kongzue:BaseJson:1.0.7.2'
}
```

#### jCenter（已停止服务）：

在dependencies{}中添加引用：
```
implementation 'com.kongzue.basejson:basejson:1.0.7'
```

## 概念
本组件对 Json 的定义如下：

对于 Json 对象，为 JsonMap，对于 Json 集合，为 JsonList。

JsonMap 是 Map 的子类，JsonList 是 List 的子类，本身各自具备 Map、List 的特点，适用于一切 Map、List 的使用场景。

可通过 JsonBean 工具类，实现 JavaBean 与 JsonMap 之间的互相转换，这里使用了反射技术。

在 get 值时，建议使用 JsonMap 和 JsonList 定义的 getString(key)、getBoolean(key) 等方法，使用 Map、List 原本的 get 依然可能获得空对象（null）。

使用 JsonMap 和 JsonList 定义的 get 类方法不会有返回空对象的情况。

## 由 String 创建 JsonMap/JsonList

使用以下代码将 Json 文本解析为 JsonMap：
```
JsonMap data = new JsonMap("{\"key\":\"DFG1H56EH5JN3DFA\",\"token\":\"124ASFD53SDF65aSF47fgT211\"}");
```

使用以下代码将 Json 文本解析为 JsonList：
```
JsonList list = new JsonList("[{\"answerId\":\"98\",\"questionDesc\":\"否\"},{\"answerId\":\"99\",\"questionDesc\":\"是\"}]");
```

若 Json 文本不合法，或内容有错误，则可能返回 **空内容** 的 JsonMap/JsonList。

## 读取值
使用 JsonMap 和 JsonList 定义的 getString(key)、getBoolean(key) 等方法不会读取空指针。

即，对于以下 Json：
```
{
    "key":"DFG1H56EH5JN3DFA",
    "token":"124ASFD53SDF65aSF47fgT211"
}
```
若执行：
```
JsonMap data = JsonMap.parse("{\"key\":\"DFG1H56EH5JN3DFA\",\"token\":\"124ASFD53SDF65aSF47fgT211\"}");

String key = data.getString("key");                     //获得“key”的值
String name = data.getString("name");                   //显然，这段 json 并没有“name”字段
JsonMap data = data.getJsonMap("data");                 //这段 json 中没有名为“data”的子 json 对象
String age = data.getJsonMap("data").getInt("age");     //更不可能有“data”下的值“age”
```
上述 get 方法均不会报空指针错误，而是会获得该类型的默认空值，即：
```
key = "DFG1H56EH5JN3DFA"
name = ""
data = new JsonMap()    //返回空的 JsonMap 对象，可使用 isEmpty() 判空
age = 0
```
对于默认状态可能为 0 的 int 类型的值，或本身可能为 "" 的 String 类型的值，可以使用如下方法来设置 defaultValue：
```
String name = data.getString("name", "zhangsan");           //若该字段为空，则返回 “zhangsan”
String age = data.getJsonMap("data").getInt("age", -1);     //若该字段为空，则返回 -1
```

弹性设计是为了避免不必要的空指针问题，但同时也兼顾判空的麻烦情况，减少代码所需要对空指针的额外判断。

#### 在 JsonList 中查找

对于 JsonList，要寻找其中**含有**已知 key=value 的 JsonMap，可使用 `.findJsonMap(key, value)` 来寻找其中符合结果的 JsonMap，例如：
```java
//查找某个符合 key=value 的结果：
JsonMap result = jsonList.findJsonMap("key", "value");
// result 不会为 null，若需要判空请使用 result.isEmpty()

//查找某个符合 key=value 的结果，并对其内容进行修改：
jsonList.findJsonMap("key", "value")
        .set("age", 3);
```

## 当 Json 存在问题
对于如下异常情况，也可以放心使用：
```
{
    "key":"DFG1H56EH5JN3DFA",
    "token":"124ASFD53SDF65aSF47fgT211",
    "name":null,
    "data":[],
    "list":"[{"limitMoney\":\"26\",\"discountMoney\":\"3\"},{\"limitMoney\":\"40\",\"discountMoney\":\"4\"}]"
    "age":"3",
    "signed":"true",
}
```
对于上述 Json，与服务端约定 data 应为 Json 对象而非集合、name 应该有值但给出为 null、age 和 signed 的类型不对，使用本框架按照以下代码，依然能够正常解析：
```
String name = data.getString("name");       //返回 “” 空值
JsonMap data = data.getJsonMap("data");     //返回空的 JsonMap 对象，可使用 isEmpty() 判空
int age = data.getInt("age");               //返回 3
boolean signed = data.getBoolean("signed"); //返回 true
JsonList list = data.getList("list");       //返回解析为 JsonList 的对象
String list = data.getList("list")
                  .getJsonMap(0)
                  .getInt("limitMoney");    //返回 26
```
可见，在 BaseJson 中，类型并不是绝对的，不同类型可以进行“软”转换，以获得最符合预期的解析结果。

你无需担心任何来自后端的异常情况，使用 BaseJson 解析都可以保证绝对不会出现空指针异常。

## 输出 Json 文本
你可以手动创建 JsonMap 或 JsonList，或使用现有的 Map、List 对象构建 JsonMap、JsonList。

使用相应的`set(...)`方法添加值，然后使用`toString()`方法构建 Json 文本。
```
JsonMap data = ...;
String jsonObj = data.toString();
```

## JsonMap 与 JavaBean 的互相转换

使用 JsonBean 工具类，实现 JavaBean 与 JsonMap 之间的互相转换，这里使用了反射技术。

请注意编译为 release 时的混淆设置，必须 keep 所有项目中用到的 JavaBean 及 com.kongzue.baseokhttp.util 下的工具类，否则可能在运行时出现问题。

### 由 JsonMap 转换为 JavaBean

例如现在已经有一个 JavaBean 对象 User，以及对应的数据 jsonMap，则：
```
User user = JsonBean.getBean(jsonMap, User.class);
```
即可将 jsonMap 中的数据对应到 User 类中。

请注意，JavaBean 对象中必须包含每个属性的对应 get、set 方法，且必须使用驼峰命名规则。

支持 boolean 类型的数据获取方法以“is”开头的命名方式，例如“boolean isVIP()”或者“boolean getVIP()”都可以。

### 由 JavaBean 转换为 JsonMap

此时已经有一个设置好数据的 User 对象 user，要转换为 JsonMap：
```
JsonMap userJson = JsonBean.setBean(user);
```
转换后即可获得 JsonMap 对象。

## 开源协议
```
Copyright Kongzue BaseJson

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
## 更新日志

请前往 [Release Page](https://github.com/kongzue/BaseJson/releases) 查看