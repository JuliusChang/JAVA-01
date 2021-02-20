package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(DemoApplication.class, args);
        ISchool iSchool = applicationContext.getBean(ISchool.class);
        System.out.println(iSchool);

        DemoApplication demoApplication = applicationContext.getBean(DemoApplication.class);
        JDBCdemo(demoApplication);
    }
    private static void JDBCdemo(DemoApplication demoApplication) {
        try (Connection con = DriverManager.getConnection(demoApplication.getUrl(), demoApplication.getUsername(), demoApplication.getPassword());
             Statement st = con.createStatement();) {
            st.execute("insert into student values (1, 'chauncy')");
            st.execute("delete from student where id = 1");
            st.execute("update foo set name = 'chauncy' where id = 1");
            ResultSet rs = st.executeQuery("select * from student");
            while (rs.next()) {
                System.out.println(String.format("id:%d, name:%s", rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
