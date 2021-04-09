# ServiceAssistant（服务助手）
## github 地址
[ServiceAssistant](https://github.com/xiaoxige/ServiceAssistant)
## 介绍
**为组件化而生， 可以轻松灵活实现组件之间的任何通信及交互。**

## 主要功能
* 支持组件之间通信 (懒加载哦)
* 支持数据注入（这个功能也必须安排上）（懒加载注入哦）

## 支持亮点
* 库特别 mini， 核心库就几个类而已
* 组件之间的任意通讯
* 组件之间的回调实现
* 组件之间的数据共享
* 上层可以轻松调用下层（A 依赖 B, 可实现 B 对 A 的任意访问）
* 轻松支持数据注入
* 更加灵活（你想怎么都行）

## 当前最新版本
| name | desc|
| :-: | :-:|
|annotation| 1.0.2|
|core| 1.0.3|
|plugin|1.0.5|
|processor| 1.0.4|

## 依赖
在项目 gradle 中配置

```groovy 
repositories {
    maven {
            url "https://gitee.com/xiaoxigexiaoan/warehouse/raw/master"
        }
    }

    dependencies {
        classpath "cn.xiaoxige.serviceassistant:plugin:xxx"
    }
}

allprojects {
    repositories {
        maven {
            url "https://gitee.com/xiaoxigexiaoan/warehouse/raw/master"
        }
    }
}
```

在 APP gradle 中配置

```groovy
# 引入插件
apply plugin: 'service-assistant'
apply plugin: 'kotlin-kapt'

# 加入核心依赖
dependencies {
	implementation 'cn.xiaoxige.serviceassistant:core:xxx'
	kapt 'cn.xiaoxige.serviceassistant:processor:xxx'
}
```

**注:**

**1. 插件只需也仅仅要在 application 中加入**

**2. 如果要使用注入功能， 记得要加上 kapt 依赖哈， 尤其在依赖库使用时不要忘记加入哦**

至此配置完成， 开启组件之旅。

## 核心思想
A 和 B 两个互不依赖的库（也包含 A 依赖 B, B 需要访问 A 的操作）。比如 B 需要访问 A, 那么必须知晓 A 提供出来了什么能力。 所以 A 需要抛出一系列接口能力（当然这些抛出的能力肯定是谁抛出谁去实现, 这里肯定是 A 实现）, B 去依赖 A 的接口， 就可以轻松访问 A 的能力。 那么 B 依赖 A 的接口， 怎么就调用到 A 的实现了呢？ 这就是该库所要做的事情！

![通讯模型](https://img-blog.csdnimg.cn/img_convert/6bdc52c9b31b4461ae220d01a261cbac.png)

## 类介绍（一共就 5 个类）
| name | desc|
| :-: | :-:|
|interface IService<T>|提供服务的服务的接口|
|annotation class Service|提供服务的注解|
|object Service| 服务获取|
|annotation class NeedInjected| 提供注入的实现类的注解|
|annotation class Injected| 变量注入注解|

**提供能力的实现一定要继承 IService 接口！！！并加入 @Service 注解！！！**

**使用注入， 在实现接口类上加入 @NeedInjected 注解！ 在使用的接口变量上加入 @Injected 注解！**

## 简易使用篇（更多操作， 比如组件回调等，请运行和参考 Demo）

比如账户组件需要登录组件的登录及用户信息
登录组件提供的 Api 接口能力中(详情可见 Demo 中的 LoginApi): 

```kotlin
interface IUserInfoApi {

    /**
     * 是否登录
     */
    fun isLogin(): Boolean

    /**
     * 获取用户 id
     */
    fun getUserId(): String
}

interface ILoginAbilityApi {

    /**
     * 登录
     */
    fun toLogin(context: Context)

    fun addLoginStateChangedListener(listener: ILoginStateChangedListener)

    fun removeLoginStateChangeListener(listener: ILoginStateChangedListener)

    interface ILoginStateChangedListener {
        fun change(state: Boolean)
    }
}
```

登录组件中相关能力实现(详情可见 LoginComponent)

```kotlin
@Service
class UserInfoApiImpl : IService<IUserInfoApi>, IUserInfoApi {
    /**
     * 使用方提供
     */
    override fun getService(): IUserInfoApi {
        return UserInfoApiImpl()
    }

    /**
     * 是否登录
     */
    override fun isLogin(): Boolean {
        return UserInfo.isLogin
    }

    /**
     * 获取用户 id
     */
    override fun getUserId(): String {
        return UserInfo.userId
    }

}

@Service
class LoginAbilityApiImpl : IService<ILoginAbilityApi>, ILoginAbilityApi {


    /**
     * 使用方提供
     */
    override fun getService(): ILoginAbilityApi {
        return LoginAbilityApiImpl()
    }
    
    override fun toLogin(context: Context) {
        LoginActivity.showActivity(context)
    }

    override fun addLoginStateChangedListener(listener: ILoginAbilityApi.ILoginStateChangedListener) {
        sLoginStateChangedListener.add(listener)
    }

    override fun removeLoginStateChangeListener(listener: ILoginAbilityApi.ILoginStateChangedListener) {
        sLoginStateChangedListener.remove(listener)
    }

    companion object {
        private val sLoginStateChangedListener =
            mutableListOf<ILoginAbilityApi.ILoginStateChangedListener>()

        fun notifyLoginState(state: Boolean) {
            sLoginStateChangedListener.forEach {
                it.change(state)
            }
        }
    }

}
```

账户组件获取登录信息 （详情可见 AccountComponent）

```kotlin
Service.getService(IUserInfoApi::class.java)?.let {
    tvUserInfo.text = if (it.isLogin()) "userId: ${it.getUserId()}" else "未登录"
}
```

账户组件去跳登录界面, 相信聪明的你已经不用我再复制了吧。 （详情可见 App, Demo 中我是在 App 去跳的登录界面）

## 注入使用
这个使用过于简单， 当然也相信使用其他注入框架跟多了， 这里就简单一个例子。

1. 定义需要注入的类：

``` kotlin
// 定义接口
interface ISettingRepo {

    fun getSettingInfo(): String
}

// 进行实现
@NeedInjected(false)
class SettingRepoImpl : ISettingRepo {

    override fun getSettingInfo(): String {
        return "网络请求得到的 setting 结果"
    }

}

```

2. 进行使用

``` kotlin
class MainActivity : AppCompatActivity() {

    @Injected
    private lateinit var mAboutRepo: IAboutRepo

    @Injected
    private lateinit var mSettingRepo: ISettingRepo
}
```

打完收工！！！

## 更新日志

### plugin -> 1.0.5
* 解决部分 windows 系统编译失败问题

### annotation -> 1.0.2, core -> 1.0.3, plugin -> 1.0.4, processor -> 1.0.4
* 注入支持跨 model 使用

### plugin -> 1.0.3, core -> 1.0.2
* 组件通讯支持懒加载

### processor -> 1.0.3, plugin -> 1.0.2
* 支持注入可在多 model 中使用

### processor -> 1.0.2
* 注入支持懒加载

### 1.0.1
* 支持了注入
* 添加了 annotation 和 processor

### 1.0.0
* 主持组件化通讯
* 加入 core 和 plugin

## 其他
**关于组件的初始化、 组件之间跳转、 组件之间的通讯、 组件之间的回调等用法效果请运行 Demo 查看效果， 其用法请参考 Demo!!!**


**欢迎 Star**
