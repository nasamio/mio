## 这是用于简化开发的仓库（23/08/17 纯java编写）

仅限个人及小伙伴使用，下面是各个模块的使用方式。

### base

base库封装基类库，基于data binding的，所以使用者还需要在模块的build.gradle中声明：

```groovy
    dataBinding {
        enabled = true
    }
```