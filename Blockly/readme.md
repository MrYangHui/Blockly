## Bolockly后端
(目前完成注册和登录)
(根据文档修改了访问地址、请求方式和返回信息,修改了数据库新增头像图片路径和邮箱地址,新增查看个人信息请求处理)
(前后端接口文档:https://www.zybuluo.com/lishuyang/note/1697828)
- 5.10
(实现了剩余接口中的, record, save 和 load)
(新增2个数据表保存record和history, 可以使用sql目录下的文件创建相应的数据表)
- 5.24
(修改了 添加用户操作记录、存储历史场景、加载历史场景 三个接口,现在userId将从session中取得,未登录无法获得userId将操作失败)
(使用websocket实现了房间的功能, 使用的请求方式为ws://localhost:8080/room/{connectParam}, 与接口文档的http请求方式不同!!)



### 数据库
- 使用mysql数据库, 请先自行安装mysql并创建名为"blockly"的数据库
- 通过sql目录下的文件建表, 目前仅user表
- 更改src/main/resource/application.properties文件中的登录mysql使用的用户名和密码(同时也可以更换运行端口)

### 当前项目结构(src/main/java)
- bean,实体类,(UserEntity)
- controller,控制类,提供各种resful访问调用,(UserController)
- service,服务器,实现逻辑业务,(UserService)
- mapper,数据库访问接口(使用mybatis),(UserMapper)

## 当前实现功能

### 用户相关

#### 注册
- 映射地址为"/data/user/register", 即访问"http://localhost:port/data/user/register"
- 请求方式为post, 需要参数userName, password, 二者为字符串类型并且非空。(目前还没有加入邮箱和头像文件路径)
- 返回值, 参见接口文档
- 具体实现参见"src/main/java/controller/UserController"

#### 登录
- 映射地址为"/data/user/login", 即访问"http://localhost:port/data/user/login"
- 请求方式为get, 需要参数userName, password, 二者为字符串类型并且非空
- 返回值, 参见接口文档
- 目前使用session保存了属性"userId",值为对应的userId,来保存登录状态
- 具体实现参见"src/main/java/controller/UserController"

#### 登出
- 映射地址为"/data/user/logout", 即访问"http://localhost:port/data/user/logout"
- 请求方式为get
- 返回值, 参见接口文档
- 将session中保存的属性"userId"移除
- 具体实现参见"src/main/java/controller/UserController"

#### 查看个人信息
- 映射地址为"/data/user/information", 即访问"http://localhost:port/data/user/information"
- 请求方式为get
- 返回值, 参见接口文档
- 从session中读取"userId", 再以id访问数据库得到用户信息，返回给前端
- 如果没有登陆, 则session中无法获取到"userId", 请求将返回错误

#### 查看用户操作记录
- 映射地址为"/data/user/record", 即访问"http://localhost:port/data/user/record"
- 请求方式为get
- 返回值, 参见接口文档
- 从session中读取"userId", 返回所有record构成的List
- 如果没有登陆, 请求将返回错误
- ps: 后端始终将操作记录中的日期和时间作为字符串处理,
以免出Java和MySQL中类型转换可能出现的问题, 所以前端的参数直接以字符串给出即可,
同时前端收到的也是字符串, 所以请留意对这两个量的处理。

#### 添加用户操作记录
- 映射地址为"/data/user/record", 即访问"http://localhost:port/data/user/record"
- 请求方式为post(与查看用户操作记录的映射地址相同, 但是二者的请求方式不同)
- 参数和返回值, 参见接口文档
- 在数据库中增加一条记录, 成功返回true

### 游戏相关

#### 存储历史场景
- 映射地址为"/data/game/save", 即访问"http://localhost:port/data/game/save"
- 请求方式为post
- 参数和返回值, 参见接口文档
- history是以longtext的类型存在数据表中的, 后端始终将其作为字符串来处理
- history只存储最近的历史，即每次save会覆盖原来的历史
- history表把userId和gameId一起作为主键, insert时使用"on duplicate key update", 保证上一条描述的行为正确

#### 加载历史场景
- 映射地址为"/data/game/load", 即访问"http://localhost:port/data/game/load"
- 请求方式为get
- 参数gameId (现在userId从session中取得)
- 查询对应的history并返回给前端即可, 没有对应记录返回空字符串""


### 房间相关
(使用springboot websocket, 不知道实现是否合理, 有任何问题请前端即时反馈!!)

以下两个接口的访问地址相同ws://localhost:8080/room/{connectParam} ,用connectParam参数做区别
#### 创建房间
- connectParam为gameId即一个整数时, 例如ws://localhost:8080/room/{1} ,创建一个该游戏的房间
- 创建成功时, 服务器将通过websocket连接返回房间的Id, 该Id是一个随机生成的8位字母和数字的组合字符串


#### 加入房间
- connectParam为roomId即一个长度为8的字母和数字的组合字符串时, 例如ws://localhost:8080/room/{abcd1234} ,加入该id对应的游戏房间
- 一个房间只能加入2人, 成功时返回信息"连接成功。",房间不存在或者已有2人加入, 将连接失败并得到对应的提示
- 加入房间后, 两用户通过websocket发送的消息，会通过服务器转发给对方, 以此为前端提供一人的操作被另一人共享的功能
- 因为目前websocket服务器的消息发送均是字符形式, 所以需要前端判断收到消息内容, 如果无法正常使用房间相关的公共请及时反馈!!

