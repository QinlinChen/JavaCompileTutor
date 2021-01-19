# JavaCompileTutor

这是一个很久以前写的用命令行编译Java项目的教程。希望有助于读者理解自动化工具背后的原理。

## 编译简单的项目

首先，我们进入`SayProject`目录，查看`cn/edu/nju/cs/Say.java`。可以看到，`Say.java`的第一行用`package`声明了`cn.edu.nju.cs`。这个叫逻辑路径，它是和物理路径（即`cn/edu/nju/cs/`）是对应的。当然，`Say.java`也可以不放在`cn/edu/nju/cs`这个物理路径下，之后也能顺利编译。

然后，我们在`SayProject`目录下编译`Say.java`：

```
$ cd SayProject
$ javac -d build cn/edu/nju/cs/Say.java
```

`-d`参数表示，编译后的class文件放入`-d`后面指定的目录里。我们发现，`javac`根据先前`package`声明的逻辑路径`cn.edu.nju.cs`自动在`build`目录下创建了`cn/edu/nju/cs`这个物理路径，并把`Say.class`放在里面。这里，我们可以感受到逻辑路径和物理路径的映射关系。

接下来执行，使用指令：

```
$ java -classpath build cn.edu.nju.cs.Say
```

具体原理是：`java`程序首先从`classpath`找到路径`build`，接着把逻辑路径`cn.edu.nju.cs.Say`映射到物理路径`cn/edu/nju/cs/Say.class`上，然后把这个物理路径接在`classpath`（即`build/`）之后，得到`build/cn/edu/nju/cs/Say.class`，最后把这个路径所代表的class文件加载到java虚拟机中，找到`main`函数运行。这里，我们再次感受到逻辑路径和物理路径的映射关系。

接下来我们做个简单的打包。我们目前还没有依赖库的存在。下面打包的这个包会作为后面示例的依赖库：

```
$ jar cvf SayLib.jar -C build .
```

这里面的`-C`参数我懒得解释了。请自己查一下手册。

这个打包（相当于用zip压缩）生成了一个`SayLib.jar`。（想象一下`SayLib.rar`——其实你可以也用解压软件打开jar包。）我们可以输入下面的指令看看`SayLib.jar`里有什么：

```
$ jar tf SayLib.jar
```

你会惊讶的发现，`jar`在打包的时候自动给你创建了一个文件`META-INF/MANIFEST.MF`。这个叫做清单文件。之后可以手动指定、修改。默认的内容如下：

```
Manifest-Version: 1.0
Created-By: 11.0.1 (Oracle Corporation)
```

下面你应该好奇怎么直接执行jar的主文件呢？依葫芦画瓢像下面这样？

```
$ java -jar SayLib.jar cn.edu.nju.cs.Say
```

它会报错说没有主清单属性。这时，我们就要创建上面所说的清单文件了。在`SayProject`目录下创建`Manifest.txt`，写入：

```
Main-Class: cn.edu.nju.cs.Say
```

记得最后要换行。然后重新打包：

```
$ jar cvfm SayLib.jar Manifest.txt -C build .
```

其中`cvfm`中的`m`选项指定了清单文件。

最后我们尝试执行 `java -jar SayLib.jar` 应该能顺利得到结果。

总结一下这一节我们用过的主要指令：

```
javac -d build cn/edu/nju/cs/Say.java
java -classpath build cn.edu.nju.cs.Say
jar cvfm SayLib.jar Manifest.txt -C build .
java -jar SayLib.jar
```

## 编译有Lib依赖的项目

我们进入`HelloProject`这个目录。然后把前面打包的`SayLib.jar`放到`HelloProject/lib`里去（如果lib目录不存在就自己创建一个）。

查看一下`Hello.java`这个文件。首先，如果你足够细心，你应该能发现`Hello.java`没有`package`声明。这是因为，它在最顶层目录，而默认的声明就是`package .`——逻辑路径"`.`"和物理路径"`./`"是一致的。接下来，`Hello.java` `import`了`utils`这个目录下的`Stopwatch`类（用作计时器）（可以看看这个类的`package`声明再熟悉一下）。另外，它也`import`了我们刚才写的类，它的逻辑路径是`cn.nju.edu.cn.Say`。物理路径的根目录需要稍后指定，比如通过`classpath`。

我们首先很自然地会想尝试用上一节的方法编译：

```
$ javac -d build Hello.java utils/Stopwatch.java
```

但错误信息会告诉我们找不到`cn.edu.nju.cs.Say`。这是因为，`javac`无法把这个逻辑路径映射到物理路径上。所以我们要在命令行参数中显式指定`classpath`：

```
$ javac -classpath lib/SayLib.jar -d build Hello.java utils/Stopwatch.java
```

这样，javac就会去外部的`lib/SayLib.jar`找`cn/edu/nju/cs/Say.class`，最后编译成功。接下来，我们尝试用老方法执行`Hello`：

```
$ java -classpath build Hello
```

`java`也告诉我们它找不到`cn.edu.nju.cs.Say`。显然，这里也要添加`lib/SayLib`.jar到`classpath`中：

```
$ java -classpath "./build;./lib/SayLib.jar" Hello
```

最后让我们尝试一下打包操作（`Manifest.txt`已经写好），不妨命名为`MyApp.jar`：

```
$ jar cvfm MyApp.jar Manifest.txt lib -C build .
```

如果这样打包，那么接下来用`java -jar MyApp.jar`来执行是会报错的。读者可以尝试一下，看看错误信息。

为了修复这个问题，我们需要在已经写好的`Manifest.txt`加上下面一行：

```
Class-Path: lib/SayLib.jar
```

记得换行。注意，这里的`lib/SayLib.jar`是相对`MyApp.jar`的路径。也就是说`lib/SayLib.jar`不用打包进`MyApp.jar`：

```
$ jar cvfm MyApp.jar Manifest.txt -C build .
```

最后`java -jar MyApp.jar`顺利执行。

让我们来总结一下这个小结用到的指令：

```
javac -classpath lib/SayLib.jar -d build Hello.java utils/Stopwatch.java
java -classpath "./build;./lib/SayLib.jar" Hello
jar cvfm MyApp.jar Manifest.txt -C build .
java -jar MyApp.jar
```