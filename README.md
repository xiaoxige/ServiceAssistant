# ServiceAssistant（服务助手）
## github 地址
[ServiceAssistant](https://github.com/xiaoxige/ServiceAssistant)
## 介绍
**为组件化而生， 可以轻松灵活实现组件之间的任何通信及交互。**

## 支持亮点
* 库特别 mini， 核心库就 3 个类， 整个库也一共就 10 个类
* 组件之间的任意通讯
* 组件之间的回调实现
* 组件之间的数据共享
* 上层可以轻松调用下层（A 依赖 B, 可实现 B 对 A 的任意访问）
* 更加灵活（你想怎么都行）

## 依赖
在项目 gradle 中配置

```groovy 
repositories {
    maven {
            url "https://gitee.com/xiaoxigexiaoan/warehouse/raw/master"
        }
    }

    dependencies {
        classpath "cn.xiaoxige.serviceassistantplugin:service-assistant-plugin:1.0.0"
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

# 加入核心依赖
dependencies {
	implementation 'cn.xiaoxige.serviceassistantplugin:core:1.0.0'
}
```

至此配置完成， 开启组件之旅。

## 核心思想
A 和 B 两个互不依赖的库（也包含 A 依赖 B, B 需要访问 A 的操作）。比如 B 需要访问 A, 那么必须知晓 A 提供出来了什么能力。 所以 A 需要抛出一系列接口能力（当然这些抛出的能力肯定是谁抛出谁去实现, 这里肯定是 A 实现）, B 去依赖 A 的接口， 就可以轻松访问 A 的能力。 那么 B 依赖 A 的接口， 怎么就调用到 A 的实现了呢？ 这就是该库所要做的事情！

![通讯模型](https://img-blog.csdnimg.cn/img_convert/b18849bf2a1e5b7fdf8e12a2f5200974.png)

## 类介绍（一共就 3 个类）
| name | desc|
| :-: | :-:|
|interface IService<T>|提供服务的服务的接口|
|annotation class Service|提供服务的注解|
|object Service| 服务获取|

**提供能力的实现一定要继承 IService 接口！！！并加入 @Service 注解！！！**

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

## 其他
**关于组件的初始化、 组件之间跳转、 组件之间的通讯、 组件之间的回调等用法效果请运行 Demo 查看效果， 其用法请参考 Demo!!!**


**如果觉得有帮助， 欢迎 Star**
