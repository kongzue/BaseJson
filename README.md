# BaseJson
基于 org.json 的改进型 Json 解析库，快速解析 Json 为 Map 或 List 对象。

<a href="https://github.com/kongzue/BaseJson/">
<img src="https://img.shields.io/badge/BaseJson-1.0.3-green.svg" alt="BaseOkHttp">
</a>
<a href="https://bintray.com/myzchh/maven/BaseJson/1.0.3/link">
<img src="https://img.shields.io/badge/Maven-1.0.3-blue.svg" alt="Maven">
</a>
<a href="http://www.apache.org/licenses/LICENSE-2.0">
<img src="https://img.shields.io/badge/License-Apache%202.0-red.svg" alt="License">
</a>
<a href="http://www.kongzue.com">
<img src="https://img.shields.io/badge/Homepage-Kongzue.com-brightgreen.svg" alt="Homepage">
</a>

## 简介
- 将 Json 转换为 Map 或 List 的子类，可直接用于 Adapter，省去编写 Bean 的过程。
- 高容错设计，无论如何绝不会出现空指针，哪怕类型错误，你也无须担心空指针异常；
- 无缝衔接 Map 和 List，可随意通过 Map 或 List 创建 Json 对象；

## Maven仓库或Gradle的引用方式
Maven仓库：
```
<dependency>
  <groupId>com.kongzue.basejson</groupId>
  <artifactId>basejson</artifactId>
  <version>1.0.3</version>
  <type>pom</type>
</dependency>
```
Gradle：

在dependencies{}中添加引用：
```
implementation 'com.kongzue.basejson:basejson:1.0.3'
```

## 概念
本组件对 Json 的定义如下：

对于 Json 对象，为 JsonMap，对于 Json 集合，为 JsonList。

JsonMap 是 Map 的子类，JsonList 是 List 的子类，本身各自具备 Map、List 的特点，适用于一切 Map、List 的使用场景。

在 get 值时，建议使用 JsonMap 和 JsonList 定义的 getString(key)、getBoolean(key) 等方法，使用 Map、List 原本的 get 依然可能获得空对象（null）。

使用 JsonMap 和 JsonList 定义的 get 类方法不会有返回空对象的情况。

## 由 String 创建 JsonMap/JsonList

使用以下代码将 Json 文本解析为 JsonMap：
```
JsonMap data = JsonMap.parse("{\"key\":\"DFG1H56EH5JN3DFA\",\"token\":\"124ASFD53SDF65aSF47fgT211\"}");
```

使用以下代码将 Json 文本解析为 JsonList：
```
JsonList list = JsonList.parse("[{\"answerId\":\"98\",\"questionDesc\":\"否\"},{\"answerId\":\"99\",\"questionDesc\":\"是\"}]");
```

若 Json 文本不合法，或内容有错误，则可能返回空内容的 JsonMap/JsonList。

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

String name = data.getString("name");
JsonMap data = data.getJsonMap("data");
String age = data.getJsonMap("data").getInt("age");
```
上述 get 方法均不会报空指针错误，而是会获得该类型的默认空值，即：
```
name = ""
data = new JsonMap() 
age = 0
```
对于默认状态可能为 0 的 int 类型的值，或本身可能为 "" 的 String 类型的值，可以使用如下方法来设置 defaultValue：
```
String name = data.getString("name", "zhangsan");           //设置若为空，则返回 “zhangsan”
String age = data.getJsonMap("data").getInt("age", -1);     //设置若为空，则返回 -1
```

弹性设计是为了避免不必要的空指针问题，但同时也兼顾判空的麻烦情况，减少代码所需要对空指针的额外判断。

## 当 Json 存在问题
对于如下异常情况，也可以放心使用：
```
{
    "key":"DFG1H56EH5JN3DFA",
    "token":"124ASFD53SDF65aSF47fgT211",
    "name":null,
    "data":[],
    "age":"3",
    "signed":"true",
}
```
对于上述 Json，与服务端约定 data 应为 Json 对象而非集合、name 应该有值但给出为 null、age 和 signed 的类型不对，使用本框架按照以下代码，依然能够正常解析：
```
String name = data.getString("name");       //返回 “” 空值
JsonMap data = data.getJsonMap("data");     //返回 new JsonMap()
int age = data.getInt("age");               //返回 3
boolean signed = data.getBoolean("signed"); //返回 true
```
你无需担心任何来自后端的异常情况，使用 BaseJson 解析都可以保证绝对不会出现空指针异常。

## 输出 Json 文本
你可以手动创建 JsonMap 或 JsonList，或使用现有的 Map、List 对象构建 JsonMap、JsonList。

使用相应的`set(...)`方法添加值，然后使用`toString()`方法构建 Json 文本。
```
JsonMap data = ...;
String jsonObj = data.toString();
```

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
v1.0.3：
- 增加通过 Map 和 List 创建 JsonMap、JsonList 的构造方法；

v1.0.2：
- 全新发布；