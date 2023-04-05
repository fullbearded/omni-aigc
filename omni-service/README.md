# OmniChat 后端服务

基于SpringBoot 3.0.5创建

基于DDD的分层架构进行项目分包，本项目由于业务复杂度不高，采用基于DDD设计并进行领域上下文切分的单体架构设计
目前分为表现层Representation、应用层ApplicationService、领域层Domain、基础设施层Infrastructure

```
 ├─omnichat
 |    ├─OmniChatApplication.java  # 应用入口
 |    ├─presentation              # 表现层: 与网页、小程序等进行交互的APIS、ExternalEvent、RPC等
 |    ├─application               # 应用层: 领域模型的调用者/客户方 如用户管理UserApplicationService，
 |    ├─domain                    # 领域层: 领域模型，对每个领域上下文的封装
 |    ├─infrastructure            # 基础设施层: 支撑表现层、应用层、领域层，封装了通用的类方法
```

DDD的典型业务处理流程：Controller.RestFulAPI -> ApplicationService.usecase -> DomainService.doBusiness -> AggreateRoot.doBussines -> Repository.saveAggregateRoot
即现实层接收VO数据，在应用层基于业务案例调用一个或者多个领域层进行业务逻辑处理，通过聚合根进行领域对象的保存

详细的目录结构如下：

```
 ├─omnichat
 |    ├─OmniChatApplication.java  # 应用程序的入口
 |    ├─presentation              # 表现层根目录
 |    |       ├─vo                #   显示层对象，通常是Web向模板渲染引擎层传输的对象
 |    |       ├─assembler         #   组装器，负责将多个domain领域对象组装为需要的dto对象，组装器中不应当有业务逻辑在里面，主要负责格式转换、字段映射等职责
 |    ├─infrastructure            # 基础设施层，主要存放整个项目的公开工具以及
 |    |       ├─redis             #   Redis工具类
 |    |       ├─log               #   日志工具类
 |    |       ├─jwt               #   JWT认证工具类
 |    |       ├─http              #   HTTP工具类
 |    |       ├─exception         #   全局异常工具类
 |    |       ├─domain            #   基础的领域实体
 |    |       ├─config            #   全局配置
 |    |       ├─common            #   公共配置，如常量配置等
 |    ├─application               # 应用层
 |    |      ├─sso                #   统一登录封装，聚合了账户、第三方登录，针对统一登录
 |    |      ├─service            #   应用层的Service类，类似
 |    ├─domain                    # 领域层
 |    |   ├─user                  #   用户中心
 |    |   |  ├─infrastructure     #     基础设施
 |    |   |  |       ├─repository #       仓库
 |    |   |  |       ├─entity     #       领域实体
 |    |   |  ├─domain             #     领域模型
 |    |   |  |   ├─service        #       领域Service
 |    |   |  |   ├─facade         #       领域仓库接口
 |    |   |  |   ├─mapper         #       领域数据库映射
```
