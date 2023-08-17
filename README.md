## 这是用于简化开发的仓库（23/08/17 纯java编写的）

仅限个人及小伙伴使用，下面是各个模块的使用方式。

### 使用：

项目根目录的build.gradle中：

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

高版本的as可能在settings.gradle中。

接着需要在你的项目的build.gradle中加入依赖：

```
dependencies {
            implementation 'com.github.nasamio:mio:Tag'
}
```

tag需要看最新的tag标签：

### base

base库封装基类库，基于data binding的，所以使用者还需要在模块的build.gradle中的android闭包中声明：

```groovy
    dataBinding {
        enabled = true
    }
```

之后点击右上角的sync now进行同步。