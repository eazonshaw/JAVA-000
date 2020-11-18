# homework
* `计算机中的所有问题都可以通过添加一层中间层来解决`

## 必做题

1. （必做）写代码实现Spring Bean的装配，方式越多越好（XML、Annotation都可以）,提
交到Github。

三种方式实现 Spring Bean 的装配，新建一个 bean ，命名为 Student。

```
public class Student {
    public void say(){
        System.out.println("I'm a student.");
    }
}
```

* 方式一：注解。给 Student 添加注解 `@Component`,在配置文件中定义扫描 bean ,单元测试读取配置文件，输出 bean 中的方法。
```
<context:component-scan base-package="spring02"/>
```
```
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "classpath:spring02/annotation/spring-config.xml")
public class SpringTest {
    @Autowired
    private Student student;

    @Test
    public void test(){
        student.say();
    }
}
```

* 方式二：xml 定义 bean。在 xml 中定义 bean，单元测试读取 xml 配置文件。
```
<bean id="student" class="spring02.bean.Student"/>
```
```
@ContextConfiguration(value = "classpath:spring02/xml/spring-bean.xml")
```

* 方式三：自动装配 bean。定义 SpringConfig，将 bean 实例自动装配。`注意：bean 的名称默认为构造函数的名称，若构造函数名称找不到，就直接找返回值`。单元测试读取该配置类。
```
@Configuration
public class SpringConfig {
    @Bean
    public Student student(){
        return new Student();
    }
}
```
```
@ContextConfiguration(classes = SpringConfig.class)
```

2. （必做）给前面课程提供的Student/Klass/School实现自动配置和Starter。

* 新建 MyConfiguration 类，配置三个bean
```
@Configuration
public class MyConfiguration {

    @Bean(name = "student00")
    public Student student(){
        return new Student().create();
    }

    @Bean(name = "class1")
    public Klass klass(){
        Klass class1 = new Klass();
        List<Student> list = new ArrayList<>();
        IntStream.range(0,10).forEach(i -> {
            list.add(new Student(i,"user_"+i));
        });
        class1.setStudents(list);
        return class1;
    }

    @Bean
    public ISchool school(){
        return new School();
    }

}
```

* 实现自动装配类 MyAutoConfiguration
```
@Configuration
@Import({MyConfiguration.class})
public class MyAutoConfiguration {
}
```

* 在 META-INF 底下新增配置文件 spring.factories：
```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  springautoconfig.configuration.MyAutoConfiguration
```

* 新建测试类：
```
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootApplication
public class SpringTest {
    @Autowired
    private ISchool school;

    @Test
    public void test(){
        school.ding();
    }
}
```

* 后台输出：
```
Class1 have 10 students and one is Student(id=101, name=KK101)
```

3. （必做）研究一下JDBC接口和数据库连接池，掌握它们的设计和用法：
1）使用JDBC原生接口，实现数据库的增删改查操作。
2）使用事务，PrepareStatement方式，批处理方式，改进上述操作。
3）配置Hikari连接池，改进上述操作。提交代码到Github。

* 使用JDBC接口实现增删改查（这里以新增为例）,首先新增一个 DbUtil 处理数据库连接和关闭：
```
public class DbUtil {

    public static final String URL = "jdbc:mysql://localhost:3306/test?characterEncoding=UTF-8&useSSL=false&rewriteBatchedStatements=true";
    public static final String USER = "root";
    public static final String PASSWORD = "root";

    private static Connection conn;

    public static Connection getConn() throws Exception{
        if(conn == null){
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return conn;
    }

    public static void close(Connection conn, PreparedStatement ps, ResultSet rs){
        if(rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
```

* 实现查询和新增：
```
@Test
public void query(){
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
        conn = DbUtil.getConn();
        ps = conn.prepareStatement("select * from user");
        rs = ps.executeQuery();
        while (rs.next()){
            System.out.println("user{id:"+rs.getInt("id")+",name:"+rs.getString("name")+"}");
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        DbUtil.close(conn,ps,rs);
    }
}
    
@Test
public void insert(){
    Connection conn = null;
    PreparedStatement ps = null;
    try {
        conn = DbUtil.getConn();
        ps = conn.prepareStatement("insert into user(name) values(?)");
        ps.setString(1,"test");
        ps.execute();
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        DbUtil.close(conn,ps,null);
    }
}
```

* 实现事务和批处理：
```
@Test
public void tx(){
    Connection conn = null;
    PreparedStatement ps = null;
    try {
        conn = DbUtil.getConn();
        conn.setAutoCommit(false);//设置不自动提交
        ps = conn.prepareStatement("insert into user(name) values(?)");
        ps.setString(1,"test");
        ps.execute();
        if(true){
            throw new Exception("anything");//主动抛出异常
        }
        conn.commit();//提交事务
    } catch (Exception e) {
        try {
            conn.rollback();//捕获异常后回滚
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        e.printStackTrace();
    } finally {
        DbUtil.close(conn,ps,null);
    }
}

@Test
public void batchInsert(){
    Connection conn = null;
    PreparedStatement ps = null;
    try {
        conn = DbUtil.getConn();
        ps = conn.prepareStatement("insert into user(name) values(?)");
        for(int i = 0;i < 10;i++){
            ps.setString(1,"user_"+i);
            ps.addBatch();
        }
        ps.executeBatch();
        ps.clearBatch();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

* 配置Hikari连接池，1）引入 maven 依赖，2）新建配置文件 hikari.properties，3）测试
```
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>2.5.1</version>
</dependency>
```

```
jdbcUrl=jdbc:mysql://localhost:3306/test?useSSL=false&useUnicode=true&characterEncoding=UTF-8
driverClassName=com.mysql.jdbc.Driver
dataSource.user=root
dataSource.password=root
dataSource.databaseName=test
dataSource.serverName=localhost
dataSource.maximumPoolSize=10
```

```
@Test
public void hikariTest(){
    HikariConfig config = new HikariConfig("/hikari.properties");
    HikariDataSource ds = new HikariDataSource(config);
    try {
        Connection conn = ds.getConnection();
        PreparedStatement ps = conn.prepareStatement("select * from user");
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            System.out.println("user{id:"+rs.getInt("id")+",name:"+rs.getString("name")+"}");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        ds.close();
    }
}
```
可从日志中看到 Hikari 的连接池的打开和关闭。
```
HikariPool-1 - Started
...
//here is the sql result
...
HikariPool-1 - Close initiated...
HikariPool-1 - Before closing stats (total=1, active=1, idle=0, waiting=0)
HikariPool-1 - After closing stats (total=0, active=0, idle=0, waiting=0)
 HikariPool-1 - Closed.
```