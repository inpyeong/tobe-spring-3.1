package user.dao;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import user.domain.User;

import java.sql.SQLException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UserDaoTest {

    public static void main(String[] args) {
        JUnitCore.main("user.dao.UserDaoTest");
    }

    @Test
    public void addAndGet() throws ClassNotFoundException, SQLException {

        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");

//        UserDao dao = new DaoFactory().userDao();
        UserDao dao = context.getBean("userDao", UserDao.class);

        User user = new User();
        user.setId("whiteship");
        user.setName("백기선");
        user.setPassword("married");

        dao.add(user);

        System.out.println(user.getId() + " 등록 성공");

        User user2 = dao.get(user.getId());
//        System.out.println(user2.getName());
//        System.out.println(user2.getPassword());
//
//        System.out.println(user2.getId() + " 조회 성공");
//        if (!user.getName().equals(user2.getName())) {
//            System.out.println("테스트 실패 (name)");
//        }
//        else if (!user.getPassword().equals(user2.getPassword())) {
//            System.out.println("테스트 실패 (password)");
//        }
//        else {
//            System.out.println("조회 테스트 성공");
//        }
        assertThat(user2.getName(), is(user.getName()));
        assertThat(user2.getPassword(), is(user.getPassword()));
    }
}
