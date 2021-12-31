## Velocity

Velocity是一个基于Java的模板引擎，其提供了一个Context容器，在java代码里面我们可以往容器中存值，然后在vm文件中使用特定的语法获取，这是velocity基本的用法，其与jsp、freemarker并称为三大视图展现技术，相对于jsp而言，velocity对前后端的分离更加彻底：在vm文件中不允许出现java代码，而jsp文件中却可以

### 简单示例

#### java代码

```java
public class HelloVelocity {

    public static void main(String[] args) {
        // 初始化模板引擎
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
        // 获取模板文件
        Template t = ve.getTemplate("hellovelocity.vm");
        // 设置变量
        VelocityContext ctx = new VelocityContext(); //velocity模板的上下文对象可以存储java对象，可以再vm模板中获取
        ctx.put("name", "Velocity");
        List<String> list = new ArrayList();
        list.add("1");
        list.add("2");
        ctx.put("list", list);
        // 输出
        StringWriter sw = new StringWriter();
        t.merge(ctx,sw);
        System.out.println(sw.toString());
    }
}
```

#### vm模板

```vm
#set($greet = 'hello')       ##设置变量
$greet $name
#foreach($item in $list)     ##遍历list
    $velocityCount		     ##当前item的索引
    $item	
    $abc					 ##如果找不到变量abc则$abc会原样输出，而 $!{abc} 这回输出空白				
    $!{abc}
#end    					 ##遍历结束
```

### 变量

#### 1.变量的定义

```vm
#set($name ="velocity”)         ##给name变量赋值为velocity

#set($hello ="hello $name”)     ##等价#set($hello ="hello velocity”)
```

#### 2.变量的使用

在模板文件中使用$name 或者 ${name} 来使用定义的变量。推荐使用 ${name} 这种格式，因为在模板中同时可能定义了类似 $name 和 $names 的两个变量，如果不选用大括号的话，引擎就没有办法正确识别 $names 这个变量。

对于一个复杂对象类型的变量，例如 $person，可以使用 ${person.name} 来访问 person 的 name 属性。值得注意的是，这里的 ${person.name} 并不是直接访问 person 的 name 属性，而是访问 person 的 getName() 方法，所以 ${person.name} 和 ${person.getName()} 是一样的。

#### 3. 变量赋值

在第一小点中，定义了一个变量，同时给这个变量赋了值。对于 Velocity 来说，变量是弱数据类型的，可以在赋了一个 String 给变量之后再赋一个数字或者数组给它。可以将以下六种数据类型赋给一个 Velocity 变量：变量引用, 字面字符串, 属性引用, 方法引用, 字面数字, 数组列表。

```vm
#set($foo = $bar) 
#set($foo ="hello”)
#set($foo.name = $bar.name)
#set($foo.name = $bar.getName($arg))
#set($foo = 123)
#set($foo = ["foo”,$bar])
```

### 循环

```vm
#foreach($element in $list)   
 This is $element
 $velocityCount               ##当前element元素在list中的下表
#end
```

Velocity 引擎会将 list 中的值循环赋给 element 变量，同时会创建一个$velocityCount 的变量作为计数，从 1 开始，每次循环都会加 1。

### 条件语句

```vm
#if(condition)
...
#elseif(condition)
...
#else
...
#end
```

### 关系操作符

Velocity 引擎提供了 AND、OR 和 NOT 操作符，分别对应 &&、|| 和 ! 例如：

```vm
#if($foo && $bar)
#end
```

### 宏

Velocity 中的宏可以理解为函数定义。定义的语法如下：

```vm
#macro(macroName arg1 arg2...)
...
#end
```

调用这个宏的语法是：

```vm
#macroName(arg1 arg2...)
```

示例

```vm
#macro(sayHello $name)
hello $name
#end
#sayHello("velocity")              ##结果显示  hello velocity
```

### \#parse 和 #include

\#parse 和 #include 指令的功能都是在外部引用文件，而两者的区别是，#parse 会将引用的内容当成类似于源码文件，会将内容在引入的地方进行解析，#include 是将引入文件当成资源文件，会将引入内容原封不动地以文本输出

foo.vm

```vm
#set($name ="velocity")
```

parse.vm

```vm
#parse("foo.vm")
$!{name}                    ##结果输出   velocity
```

include.vm

```vm
#include("foo.vm")
$!{name}                    ##结果输出   #set($name ="velocity")
```

