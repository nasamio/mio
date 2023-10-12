## 这是用于简化开发的仓库（纯java编写）

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

**base activity使用:**

```
public class MainActivity extends BaseActivity<ActivityMainBinding> {
    @Override
    protected void initView() {
        // 在这里编写页面初始化逻辑
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }
}
```

**base fragment使用:**

```
public class Test2Fragment extends BaseFragment<FragmentTestBinding> {
    @Override
    protected void initView() {
        // 在这里编写页面初始化逻辑
        mDataBinding.tvContent.setText("我是测试fragment2的内容...");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_test;
    }
}

```

**baseview使用：**

```
public class TestView extends BaseView<ViewTestBinding> {
    public TestView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(AttributeSet attrs) {
        // 初始化逻辑
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_test;
    }
}
```

**fragment切换：**

```
  toFragmentWithTransition(new TestFragment(),
                        new FragmentAnimation().setTransition(FragmentAnimation.LEFT))；
```
### 下面是一些测试的demo，分属在不同的模块下

## 贪吃蛇
![screenshot](https://github.com/nasamio/mio/blob/master/img_tcs.jpg?raw=true)