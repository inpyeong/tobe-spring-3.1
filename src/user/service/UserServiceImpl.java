package user.service;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import user.dao.UserDao;
import user.domain.Level;
import user.domain.User;

import javax.sql.DataSource;
import java.security.spec.ECField;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UserServiceImpl implements UserService {
    private UserDao userDao;
    private MailSender mailSender;

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void upgradeLevels() {
        List<User> users = userDao.getAll();
        for(User user : users) {
            if (canUpgradeLevel(user)) {
                upgradeLevel(user);
            }
        }
    }

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        Boolean ret = false;
        switch(currentLevel) {
            case BASIC:
                ret = (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
                break;
            case SILVER:
                ret = (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
                break;
//            default: throw new IllegalArgumentException("Unknown Level: " + currentLevel);
        }

        return ret;
    }

    protected void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
        sendUpgradeEmail(user);
    }

    public void add(User user) {
        if(user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }

    private void sendUpgradeEmail(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("useradmin@ksug.org");
        mailMessage.setSubject("Upgrade 안내");
//        mailMessage.setText("사용자님의 등급이 " + user.getLevel().name());
        mailMessage.setText(user.getLevel().name());

        this.mailSender.send(mailMessage);
    }

}
