# dragon
dragon是一个支持分库分表,读写分离数据源,实现了jdbc规范.
>目前支持分库的数据源有很多,如:
> * 淘宝的tddl
> * 大众点评的zebra
> * 当当网的sharding-jdbc
>
> dragon并不是重复造轮子,而是从笔者使用的经验上来看,这些产品或多或少的存在一些问题.

## 1 确定分库分表数量

假设我们现在有一个dragon_sharding库,里面有1张表user:
<table border="1">
    <tr>
      <th>database</th>
      <th>table</td>
    </tr>
    <tr>
      <td rowspan="2">dragon_sharding</td>
      <td>user</td>
    </tr>
</table>

现在将user拆分成4个表,分别存放到2个库,如下所示:

<table border="1">
    <tr>
      <th>database</th>
      <th>table</td>
    </tr>
    <tr>
      <td rowspan="2">dragon_sharding_0</td>
      <td>user_0</td>
    </tr>
    <tr>
      <td>user_1</td>
    </tr>
    <tr>
        <td rowspan="2">dragon_sharding_1</td>
        <td>user_2</td>
    </tr>
    <tr>
        <td>user_3</td>
    </tr>
</table>

拆分后,我们把原来的库dragon_sharding称之为逻辑库(<code>logic database</code>),下面包含两个物理库(physical database):dragon_sharding_0,dragon_sharding_1

类似的,我们把原来的表user称之为逻辑表(<code>logic table</code>),下面包含4个物理表(physical table):user_0,user_1,user_2,user_3

>提示:

>1.具体拆分成多少个库和多少个表,根据实际情况决定,这里为了简单,只划分为2个库4张表.

>2.关于拆分后的库和表的名字没有固定的要求,但是必须要和路由规则相配合,而不是随意命名. dragon项目归纳了3种分库分表命名风格.并且提供了相应的路由规则配置的模板.
 上述案例的命名风格属于<code>库名表名序列递增风格</code>,即分库和分表的命名都是按照数量递增的.这种风格并不是dragon所推荐的,但是因为比较容易理解,所以当做案例进行讲解.

## 2 路由规则(Route Rule)

路由规则本质上就是一个计算公式,包括两个部分: 分表的路由规则(table route rule) 和 分库的路由规则(database route rule).路由规则中通过对路由字段(route column)进行运算,以确定落到哪个分库哪个分表.

假设我们把user表的主键id字段作为路由字段,希望按照以下方式进行路由:

    id%4,那么取值范围是[0-3].当id%4=0就落到user_0表,当id%4=1就落到user_1表,以此类推;

那么路由规则按照如下:

* 表的路由规则:id%4
* 库的路由规则:(id%4)/2

其中4表示的是分表的总数量,2表示的分库的总数量

对于id=1的情况:id%4=1,应该落在user_1表中;(id%4)/2=0,应该落在dargon_sharding_0库中.

对于id=2的情况:id%4=2,应该落在user_2表中;(id%4)/2=1,应该落在dargon_sharding_1库中.

对于id=3的情况:id%4=3,应该落在user_3表中;(id%4)/2=1,应该落在dargon_sharding_1库中.

对于id=4的情况:id%4=0,应该落在user_0表中;(id%4)/2=0,应该落在dargon_sharding_0库中.

> 提示:

> 1 随着分库分表的数量的不同,以及表命名格式的不同,路由规则也要进行相应的改变

> 2 对于同一个id, 表的路由规则和库的路由规则计算出来的结果一定要是匹配的. 例如上面的案例中,如果把库的路由规则改为id%2.那么对于id=1的情况,我们计算出来的分表是user_1,计算出来的分库是dragon_sharding_1.但是dragon_sharding_1中,并没有user_1这个分表.


## 3 数据源配置

数据源配置指的是配置DragonShardingDatasource.首先要理解dragon与普通的数据源如druid,c3p0,dbcp的区别在哪里.

druid这样的数据源的特点是,针对单个数据库实例进行操作,可以直接从数据库中获取真实连接.

而在分库分表的情况下,存在多个分库,很显然的,我们立马可以想到建立多个druid数据源实例,每个实例连接不同的库.但是光建立连接还不行,还有一些其他工作要做:

例如:在没有分库之前,要查询id=1的用户,只需要使用类似以下sql即可:

    select * from user where id=1;

而分库后,根据我们之前确定的路由规则,必须先要找到与分库建立连接的数据源,才能执行相应的sql:

    //通过与dragon_sharding_0库建立连接的数据源获取连接,执行以下sql
    select * from user_0 where id=4;
    select * from user_1 where id=1;

    //通过与dragon_sharding_1库建立连接的数据源获取连接,执行以下sql
    select * from user_2 where id=2;
    select * from user_3 where id=3;

注意这对原来的sql进行了改写,因为分库分表后,已经不存在user的这样一个表了.取而代之的是user_0,user_1,user_2,user_3.
另外,上述sql语句查询的是某一条记录,对于一些sql语句,可能要操作多个库,如:

    select count(*) from user;

在分库分表后,需要分别统计出每个分表的总记录数,再加在一起.这是比较麻烦的过程.

幸运的是,dragon帮我们屏蔽了所有这些繁琐的过程,它相当于一个管理者,管理的是与各个分库建立连接的数据源.

当前我们需要一些配置,才能使得dragon可以正常的工作.以上面的dragon_sharding库user表拆分为例,相应的配置如下所示:

dragon-sharding.properties

    #===============================数据源配置开始，配置项key以datasource开头=====================================
    datasource.namePattern=dragon_sharding_{0}
    datasource.list=dragon_sharding_0,dragon_sharding_1

    datasource.datasourceClass=com.alibaba.druid.pool.DruidDataSource

    #数据源属性配置 格式:datasource.<datasourceIndex>.<propertyName>
    datasource.default.username=root
    datasource.default.password=shxx12151022
    datasource.default.driverClassName=com.mysql.jdbc.Driver

    datasource.dragon_sharding_0.url=jdbc:mysql://localhost:3306/dragon_sharding_0?useSSL=false
    datasource.dragon_sharding_1.url=jdbc:mysql://localhost:3306/dragon_sharding_1?useSSL=false

    #==============================逻辑表配置开始,配置项key以logicTable开头=======================================
    logicTable.list=user
    logicTable.user.namePattern=user_{0}
    logicTable.user.dbRouteRules=(id%4)/2
    logicTable.user.tbRouteRules=id%4

配置说明:

datasource.namePattern:

        数据源命名格式.dragon中管理了多个物理数据源,为了方便管理,每个物理数据源都有一个名字.这些名字满足一个统一的格式.这个配置项的值会被构造成一个java.text.MessageFormat对象
            MessageFormat nameFormat=new MessageFormat("dragon_sharding_{0}");
        当执行一个sql时,如: select * from user where id=1;
        dragon会根据sql中的逻辑表user,找出对应的路由规则(见倒数第二个配置项):(id%4)/2,然后将sql中的路由条件id=1代入计算,结果为0.
        最后将0,传递给format对象进行格式化,得到真实的数据源名称.
            int id=1;
            int dbIndex=(id%4)/2;//结果为0
            String dbName=nameFormat.format(new Object[]{dbIndex});//得到的结果为dragon_sharding_0
        需要注意的是,这里计算出来的dragon_sharding_0只是数据源的名称,而不是库名.不过细心的读者会发现,这个数据源名称,刚好有一个分库的名称与之对应.
        这是Dragon推荐的方式,将数据源的命名格式与分库的命名格式设为相同,这样只要根据数据源的名称,就能知道操作的是哪一个库

datasource.list:

    这个配置项列出了所有物理数据源的名称.在执行一些sql,例如:select count(*) from user;由于没有指定路由条件,因此需要操作所有库,然后对结果集进行合并.
    对于dragon来说,这就意味着要找到所有的数据源,获取连接,然后分别执行改写后的sql,合并结果.因此需要这样一个列表

datasource.datasourceClass

    由dragon管理的物理数据源的类型,这里使用的是druid.因为我们在datasource.list中列出了2个数据源名称,因此dragon会自动帮我们初始化2个druid数据库连接池对象

datasource.default.[propertyName]

    物理数据源默认配置.上面列出的包括以下三项:
    datasource.default.username=root
    datasource.default.password=root
    datasource.default.driverClassName=com.mysql.jdbc.Driver
    由dragon管理的多个物理数据源,有些配置是相同的.例如,为了方便,dba通常会将每个分库的username/password都设置为相同的.
    这个配置想的作用就是将一些公共的,通用配置项放在一起,而不用每个物理数据源都要重复配置.
    特别需要注意的是:对于propertyName部分,不同的数据源是不同的.

datasource.[datasourceIndex].[propertyName]

    上面的配置项提供了物理数据源的默认配置.但是,每个物理数据源总有些配置项是不同的,例如url:
    datasource.dragon_sharding_0.url=jdbc:mysql://localhost:3306/dragon_sharding_0?useSSL=false
    datasource.dragon_sharding_1.url=jdbc:mysql://localhost:3306/dragon_sharding_1?useSSL=false
    这个时候就需要进行分别配置.配置项key中间部分,表示的是给哪一个物理数据源配置的属性,且必须要datasource.list配置项中列出

    此外,对于如果有些参数不同,也可以在这里进行覆盖,如:
    datasource.dragon_sharding_1.username=tianshouzhi
    表示数据源dragon_sharding_1的username不使用默认配置,而是改为tianshouzhi

logicTable.list

    列出所有要进行拆分的逻辑表名.

logicTable.[logicTableName].namePattern

     表示一个逻辑表的命名格式, 中间部分的logicTableName是个变量,取值为范围为logicTable.list配置项列出的逻辑表名.
     改配置项值,也会被构建成一个MessageFormat对象,dragon在执行某个sql时,会根据这个namePattern以及下面将要提到的tbRouteRules,计算出真实表名,
     替换掉sql中逻辑表名.

logicTable.[logicTableName].dbRouteRules

    库的路由规则,标准的groovy表达式.这里配置的值是(id%4)/2
    其中id是路由字段,在执行sql时,dragon会自动提取出id字段对应的值,如
    select * from user where id=1;
    提取出的id值为1 ,然后将其作为变量传入库路由规则表达式(id%4)/2中进行计算,得到库的编号,最后通过datasource.namePattern变量,来计算出真实要操作的数据源的名称

logicTable.[logicTableName].tbRouteRules

    表的路由规则,标准的groovy表达式. id%4
    其中id是一个变量,在执行sql时,dragon会自动提取出id字段对应的值,如
     select * from user where id=1;
     提取出的id值为1 ,然后将其作为变量传入表路由规则表达式id%4中进行计算,得到表的编号,最后通过logicTable.[logicTableName].namePattern变量,计算出真实表名.替换sql中的逻辑表名.
     例如以上sql会被改写为:
     select * from user_1 where id=1;








