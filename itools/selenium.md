## selenium

```java
 		ChromeDriver driver = new ChromeDriver();  //创建谷歌浏览器的驱动
        try {
            driver.get("http://www.baidu.com");    //打开百度
            driver.manage().window().maximize();//最大化
            WebElement kw = driver.findElementById("kw");  //定位输入框
            kw.click();   //点击
            kw.sendKeys("selenium");   //在输入框中输入"selenium"
            driver.findElementById("su").click();  //定位“百度一下” 并点击
            //Thread.sleep(Integer.MAX_VALUE);
        }finally {
            driver.quit(); //退出
        }
```

### 浏览器导航

#### 导航

启动浏览器后，您要做的第一件事就是打开您的网站。这可以在一行中完成：

```java
//Convenient
driver.get("https://selenium.dev");

//Longer way
driver.navigate().to("https://selenium.dev");
```

#### 获取当前URL

您可以使用以下方法从浏览器的地址栏中读取当前URL：

```java
driver.getCurrentUrl();
```

#### 回退

按下浏览器的后退按钮：

```java
driver.navigate().back();
```

#### 前进

按下浏览器的前进按钮：

```java
driver.navigate().forward();
```

#### 刷新

刷新当前页面：

```java
driver.navigate().refresh();
```

#### 获取标题

您可以从浏览器中读取当前页面标题：

```java
driver.getTitle();
```

### Windows和标签页

### 获取窗口句柄

WebDriver不会在窗口和选项卡之间进行区分。如果您的站点打开一个新的选项卡或窗口，Selenium将允许您使用窗口句柄来使用它。每个窗口都有一个唯一的标识符，该标识符在单个会话中保持不变。您可以使用以下方法获取当前窗口的窗口句柄：

```java
driver.getWindowHandle();
```

### 切换视窗或标签

点击一个在 [新窗户](https://seleniumhq.github.io/) 将使新窗口或选项卡聚焦在屏幕上，但是WebDriver将不知道操作系统认为哪个窗口处于活动状态。要使用新窗口，您将需要切换到它。如果仅打开两个选项卡或窗口，并且知道从哪个窗口开始，则通过消除过程，您可以遍历WebDriver可以看到的两个窗口或选项卡，并切换到非原始窗口或选项卡。

但是，Selenium 4提供了新的API [新窗户](https://selenium.dev/documentation/en/webdriver/browser_manipulation/#create-new-window-or-new-tab-and-switch) 这会创建一个新标签页（或新窗口）并自动切换到该标签页。

```java
//存储当前窗口的id
String originalWindow = driver.getWindowHandle();

//检查是否有其他的窗口打开了
assert driver.getWindowHandles().size() == 1;

//点击链接打开一个新的窗口
driver.findElement(By.linkText("new window")).click();

//为这个新窗口等待
wait.until(numberOfWindowsToBe(2));

//找到新的窗口的句柄并切换到新窗口
for (String windowHandle : driver.getWindowHandles()) {
    if(!originalWindow.contentEquals(windowHandle)) {
        driver.switchTo().window(windowHandle);
        break;
    }
}
//等待新窗口完成加载
wait.until(titleIs("Selenium documentation"));
```

#### 创建新窗口（或）新标签并切换

创建一个新窗口（或）选项卡，并将新窗口或选项卡聚焦在屏幕上。您无需切换为使用新窗口（或）标签。如果除了新窗口之外还打开了两个以上的窗口（或）选项卡，则可以循环浏览WebDriver可以看到的两个窗口或选项卡，然后切换到非原始窗口或选项卡。

**注意：此功能适用于Selenium 4和更高版本。**

```java
// 打开新的标签并切换
driver.switchTo().newWindow(WindowType.TAB);

// 打开新的窗口并切换
driver.switchTo().newWindow(WindowType.WINDOW);
```

#### 关闭窗口或标签

完成窗口或选项卡后*，*它又不是浏览器中打开的最后一个窗口或选项卡，则应将其关闭并切换回以前使用的窗口。假设您遵循了上一节中的代码示例，则将先前的窗口句柄存储在变量中。放在一起，您将获得：

```java
//关闭标签或窗口
driver.close();

//切换到原来的窗口
driver.switchTo().window(originalWindow);
```

### 框架和iframe

框架是现在不建议使用的方法，它可以从同一域中的多个文档构建网站布局。除非您使用HTML5之前的Webapp，否则您不太可能与他们合作。iframe允许从完全不同的域插入文档，并且仍然很常用。

如果需要使用框架或iframe，WebDriver允许您以相同的方式使用它们。考虑一下iframe中的按钮。如果使用浏览器开发工具检查元素，则可能会看到以下内容：

```html
<div id="modal">
  <iframe id="buttonframe" name="myframe"  src="https://seleniumhq.github.io">
   <button>Click here</button>
 </iframe>
</div>
```

如果不是iframe，我们希望使用类似以下内容的按钮：

```java
//不能工作
driver.findElement(By.tagName("button")).click();
```

但是，如果iframe外部没有按钮，则可能会收到一个*没有此类元素的*错误。发生这种情况是因为Selenium仅了解顶级文档中的元素。要与按钮交互，我们将需要首先切换到框架，类似于切换窗口的方式。WebDriver提供了三种切换到框架的方式。

#### 使用WebElement

使用WebElement进行切换是最灵活的选择。您可以使用首选选择器找到框架并切换到该框架。

```java
//获取iframe
WebElement iframe = driver.findElement(By.cssSelector("#modal>iframe"));

//切换到iframe
driver.switchTo().frame(iframe);

//这是就可以点击按钮了
driver.findElement(By.tagName("button")).click();
```

#### 使用名称或ID

如果您的框架或iframe具有id或name属性，则可以使用它。如果名称或ID在页面上不是唯一的，则第一个找到的名称或ID将被切换到。

```java
//使用id
driver.switchTo().frame("buttonframe");

//使用name
driver.switchTo().frame("myframe");

//这是就可以点击按钮了
driver.findElement(By.tagName("button")).click();
```

#### 使用索引

也可以使用框架的索引，例如可以使用JavaScript中的*window.frames*查询。

```java
//切换到第二个iframe
driver.switchTo().frame(1);
```

#### 离开框架

要保留iframe或框架集，请切换回默认内容，如下所示：

```java
driver.switchTo().defaultContent();
```

### 窗口管理

屏幕分辨率会影响Web应用程序的呈现方式，因此WebDriver提供了用于移动和调整浏览器窗口大小的机制。

#### 获取窗口大小

```java

//分别访问每个维度
int width = driver.manage().window().getSize().getWidth();
int height = driver.manage().window().getSize().getHeight();

//或存储尺寸并稍后查询
Dimension size = driver.manage().window().getSize();
int width1 = size.getWidth();
int height1 = size.getHeight();
```

#### 设定视窗大小

恢复窗口并设置窗口大小。

```java
driver.manage().window().setSize(new Dimension(1024, 768));
```

#### 获取窗口位置

获取浏览器窗口左上角的坐标。

```java
// 分别访问每个维度
int x = driver.manage().window().getPosition().getX();
int y = driver.manage().window().getPosition().getY();

// 或存储位置并稍后查询
Point position = driver.manage().window().getPosition();
int x1 = position.getX();
int y1 = position.getY();
```

#### 设定视窗位置

将窗口移到所选位置。

```java
// 将窗口移到主显示器的左上方
driver.manage().window().setPosition(new Point(0, 0));
```

#### 最大化窗口

放大窗口。对于大多数操作系统，窗口将填满整个屏幕，而不会阻塞操作系统自己的菜单和工具栏。

```java
driver.manage().window().maximize();
```

#### 最小化窗口

最小化当前浏览上下文的窗口。此命令的确切行为特定于各个窗口管理器。

最小化窗口通常会将窗口隐藏在系统托盘中。

**注意：此功能适用于Selenium 4和更高版本。**

```java
driver.manage().window().minimize();
```

#### 全屏窗口

填充整个屏幕，类似于在大多数浏览器中按F11。

```java
driver.manage().window().fullscreen();
```

#### 截屏

用于捕获当前浏览上下文的屏幕截图。WebDriver端点[屏幕截图](https://www.w3.org/TR/webdriver/#dfn-take-screenshot) 返回以Base64格式编码的屏幕截图。

```java
File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
FileUtils.copyFile(scrFile, new File("./image.png"));
```

#### TakeElement屏幕截图

用于捕获当前浏览上下文的元素的屏幕快照。WebDriver端点[屏幕截图](https://www.w3.org/TR/webdriver/#take-element-screenshot) 返回以Base64格式编码的屏幕截图。

```java
WebElement element = driver.findElement(By.cssSelector("h1"));
File scrFile = element.getScreenshotAs(OutputType.FILE);
FileUtils.copyFile(scrFile, new File("./image.png"));
```

#### 执行脚本

在所选框架或窗口的当前上下文中执行JavaScript代码段。

```java
//通过类型转换创建JavascriptExecutor接口对象
JavascriptExecutor js = (JavascriptExecutor)driver;
//按钮元素
WebElement button =driver.findElement(By.name("btnLogin"));
//通过点击按钮执行js
js.executeScript("arguments[0].click();", element);
//得到返回值
String text = (String) js.executeScript("return arguments[0].innerText", element);
//直接执行JavaScript
js.executeScript("console.log('hello world')");
```

#### 打印页面

在浏览器中打印当前页面。

*注意：这要求Chromium浏览器处于无头模式*

```java
printer = (PrintsPage) driver;

PrintOptions printOptions = new PrintOptions();
printOptions.setPageRanges("1-2");

Pdf pdf = printer.print(printOptions);
String content = pdf.getContent();
```

### 等待

WebDriver通常可以说具有阻塞API。因为它是一个进程外的库， *指示*浏览器该做什么，并且因为Web平台具有本质上异步的性质，所以WebDriver不会跟踪DOM的活动实时状态。这带来了一些挑战，我们将在这里讨论。

根据经验，由于使用Selenium和WebDriver而引起的大多数间歇性问题都与浏览器和用户说明之间发生的*争用情况*有关。一个示例可能是用户指示浏览器导航到页面，然后在尝试查找元素时收到**没有此类元素**错误。

考虑以下文档：

```html
<!doctype html>
<meta charset=utf-8>
<title>Race Condition Example</title>

<script>
  var initialised = false;
  window.addEventListener("load", function() {
    var newElement = document.createElement("p");
    newElement.textContent = "Hello from JavaScript!";
    document.body.appendChild(newElement);
    initialised = true;
  });
</script>
```

#### 明确等待

Selenium客户可以使用命令式，程序性语言进行*显式等待*。它们允许您的代码暂停程序执行或冻结线程，直到传递给它的*条件*解决为止。以一定的频率调用该条件，直到等待超时超时为止。这意味着只要条件返回虚假值，它将一直尝试并等待。

由于显式等待使您可以等待条件发生，因此它们非常适合在浏览器及其DOM和WebDriver脚本之间同步状态。

为了纠正前面提到的错误指令集，我们可以使用一个等待来等待*findElement*调用，直到脚本中动态添加的元素已添加到DOM中为止：

```java
driver.findElement(By.name("q")).sendKeys("cheese" + Keys.ENTER);
// 初始化并等待element（link）变为可单击-超时10秒
WebElement firstResult = new WebDriverWait(driver, Duration.ofSeconds(10))
        .until(ExpectedConditions.elementToBeClickable(By.xpath("//a/h3")));
System.out.println(firstResult.getText());
```

我们将*条件*作为函数引用传入，该*等待*将重复运行，直到其返回值为true。“真实的”返回值是在手头语言中评估为布尔值true的任何值，例如字符串，数字，布尔值，对象（包括*WebElement*）或填充的（非空）序列或列表。这意味着*空列表的结果*为false。当条件为真且阻塞等待被中止时，该条件的返回值将成为等待的返回值。

有了这些知识，并且由于默认情况下wait工具*不会*忽略*任何此类元素*错误，因此我们可以将指令重构为更加简洁：

```java
WebElement foo = new WebDriverWait(driver, Duration.ofSeconds(3))
          .until(driver -> driver.findElement(By.name("q")));
assertEquals(foo.getText(), "Hello from JavaScript!");
```

在该示例中，我们传入了一个匿名函数（但我们也可以像我们之前所做的那样显式定义它，以便可以重用）。传递给我们的条件的第一个也是唯一的参数始终是对驱动程序对象*WebDriver*的引用。在多线程环境中，应该小心操作传递给条件的驱动程序引用，而不是外部作用域中对驱动程序的引用。

因为等待不会吞没*找不到元素*时引发的*此类元素*错误，所以条件将重试直到找到元素。然后它将使用返回值*WebElement*，并将其传递回我们的脚本。

如果条件失败，例如从未达到该条件的真实返回值，则等待将抛出/引发一个错误/异常，称为*超时错误*。

#### 选件

可以根据您的需求定制等待条件。有时不必等待默认超时的全部时间，因为未达到成功条件的代价可能会很高。

通过等待，您可以传入一个参数来覆盖超时：

```java
new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.elementToBeClickable(By.xpath("//a/h3")));
```

#### 预期条件

因为必须同步DOM和您的指令是很常见的事，所以大多数客户端还带有一组预定义的*预期条件*。顾名思义，它们是为频繁的等待操作预定义的条件。

不同语言绑定中可用的条件各不相同，但这不是其中的一些穷举列表：

- 警报存在
- 元素存在
- 元素可见
- 标题包含
- 标题是
- 元素陈旧
- 可见文字

#### 隐式等待

还有第二种等待方式，与 [明确的等待](https://www.selenium.dev/documentation/en/webdriver/waits/#explicit-wait)称为*隐式等待*。通过隐式等待，WebDriver在尝试查找*任何*元素时会轮询DOM一定时间。当网页上的某些元素无法立即使用并且需要一些时间来加载时，这很有用。

默认情况下，隐式等待元素出现是禁用的，需要基于每个会话手动启用。混合[明确的等待](https://www.selenium.dev/documentation/en/webdriver/waits/#explicit-wait) 隐式等待会导致意想不到的后果，即即使元素可用或条件为true，也要等待睡眠最长时间。

*警告：* 请勿混合使用隐式和显式等待。这样做可能导致无法预测的等待时间。例如，将隐式等待设置为10秒，将显式等待设置为15秒，则可能导致20秒后发生超时。

隐式等待是告诉WebDriver在尝试查找一个或多个元素（如果它们不立即可用）时轮询DOM一定时间。默认设置为0，表示已禁用。设置后，将在会话生命周期内设置隐式等待。

```java
driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
driver.get("http://somedomain/url_that_delays_loading");
WebElement myDynamicElement = driver.findElement(By.id("myDynamicElement"));
```

#### FluentWait流利的等待

FluentWait实例定义了等待条件的最长时间，以及检查条件的频率。

用户可以配置等待以在等待时忽略特定类型的异常，例如 `NoSuchElementException` 在页面上搜索元素时。

```java
// 等待30秒，页面上会显示一个元素，然后检查
// 每5秒一次。
Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
  .withTimeout(Duration.ofSeconds(30))
  .pollingEvery(Duration.ofSeconds(5))
  .ignoring(NoSuchElementException.class);

WebElement foo = wait.until(new Function<WebDriver, WebElement>() {
  public WebElement apply(WebDriver driver) {
    return driver.findElement(By.id("foo"));
  }
});
```

### JavaScript警报，提示和确认

WebDriver提供了一个API，用于处理JavaScript提供的三种类型的本机弹出消息。这些弹出窗口由浏览器设置样式，并提供有限的自定义。

#### Alerts快讯

其中最简单的称为警报，它显示一条自定义消息，以及一个用于关闭该警报的按钮，在大多数浏览器中标记为“确定”。在大多数浏览器中，也可以通过按“关闭”按钮将其关闭，但这始终与“确定”按钮具有相同的作用。查看警报示例。

WebDriver可以从弹出窗口中获取文本，并接受或关闭这些警报。

```java
//单击链接以激活警报
driver.findElement(By.linkText("See an example alert")).click();

//等待警报显示并将其存储在变量中
Alert alert = wait.until(ExpectedConditions.alertIsPresent());

//将警报文本存储在变量中
String text = alert.getText();

//按确定按钮
alert.accept();
```

#### Confirm确认

确认框类似于警报，不同之处在于用户还可以选择取消消息。 查看样品确认。

此示例还显示了另一种存储警报的方法：

```java
//单击链接以激活警报
driver.findElement(By.linkText("See a sample confirm")).click();

//等待警报显示
wait.until(ExpectedConditions.alertIsPresent());

//将警报存储在变量中
Alert alert = driver.switchTo().alert();

//将警报存储在变量中以供重用
String text = alert.getText();

//按取消按钮
alert.dismiss();
```

#### Prompt提示

提示与确认框相似，不同之处在于它们还包括文本输入。与处理表单元素类似，您可以使用WebDriver的send键来填充响应。这将完全替换占位符文本。按下取消按钮将不会提交任何文本。 查看示例提示。

```java

//单击链接以激活警报
driver.findElement(By.linkText("See a sample prompt")).click();

//等待警报显示并将其存储在变量中
Alert alert = wait.until(ExpectedConditions.alertIsPresent());

//输入您的讯息
alert.sendKeys("Selenium");

//按确定按钮
alert.accept();
```

### Http代理

代理服务器充当客户端和服务器之间的请求的中介。简单来说，流量将通过代理服务器流向您请求的地址并返回。

使用Selenium的自动化脚本代理服务器可能对以下方面有所帮助：

- 捕获网络流量
- 网站发出的模拟后端通话
- 在复杂的网络拓扑结构或严格的公司限制/政策下访问所需的网站。

如果您在公司环境中，并且浏览器无法连接到URL，则最有可能是因为环境需要访问代理。

Selenium WebDriver提供了一种代理设置的方法：

```java
Proxy proxy = new Proxy();
proxy.setHttpProxy("<HOST:PORT>");
ChromeOptions options = new ChromeOptions();
options.setCapability("proxy", proxy);
WebDriver driver = new ChromeDriver(options);
```

### 页面加载策略

定义当前会话的页面加载策略。默认情况下，当Selenium WebDriver加载页面时，它遵循*正常的*pageLoadStrategy。始终建议您在页面加载花费大量时间时停止下载其他资源（例如图像，css，js）。

的 `document.readyState`文档的属性描述当前文档的加载状态。默认情况下，WebDriver将推迟响应`driver.get()` （要么） `driver.navigate().to()` 调用直到文档就绪状态为 `complete`

在SPA应用程序（如Angular，React，Ember）中，一旦动态内容已加载（即，如果pageLoadStrategy状态为COMPLETE），则单击链接或在页面内执行某些操作将不会向服务器发出新请求，因为内容在客户端动态加载，而无需刷新整个页面。

SPA应用程序可以动态加载许多视图，而无需任何服务器请求，因此pageLoadStrategy将始终显示 `COMPLETE` 状态，直到我们做一个新的 `driver.get()` 和 `driver.navigate().to()`

WebDriver *pageLoadStrategy*支持以下值：

#### normal正常

这将使Selenium WebDriver等待整个页面加载。设置为**normal时**，Selenium WebDriver会一直等到 [加载](https://developer.mozilla.org/en-US/docs/Web/API/Window/load_event) 返回事件火。

默认情况下，如果未提供浏览器，则将**normal**设置为浏览器。

```java
ChromeOptions chromeOptions = new ChromeOptions();
chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
WebDriver driver = new ChromeDriver(chromeOptions);
```

#### eager急于

这将使Selenium WebDriver等待直到完全加载并解析了初始HTML文档，并放弃了样式表，图像和子帧的加载。

设置为**eager时**，Selenium WebDriver等待直到 [DOMContentLoaded](https://developer.mozilla.org/en-US/docs/Web/API/Document/DOMContentLoaded_event) 返回事件火。

```java
ChromeOptions chromeOptions = new ChromeOptions();
chromeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);
WebDriver driver = new ChromeDriver(chromeOptions);
```

#### none

设置为**none时，** Selenium WebDriver仅等待下载初始页面。

```java
ChromeOptions chromeOptions = new ChromeOptions();
chromeOptions.setPageLoadStrategy(PageLoadStrategy.NONE);
WebDriver driver = new ChromeDriver(chromeOptions);
```

