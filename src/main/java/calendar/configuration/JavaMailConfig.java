//package calendar.configuration;
//
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Properties;
//
//@Configuration
//public class JavaMailConfig {
//
//    /**
//     * setting the host, port, username, password
//     *
//     * @return the mailSender
//     */
//    @Bean
//    public JavaMailSender getJavaMailSender() {
//        Properties prop = new Properties();
//        try (InputStream inputStream = chatApp.class.getResourceAsStream("/mail.properties")) {
//
//            prop.load(inputStream);
//
//        } catch (IOException e) {
//            e.printStackTrace(System.out);
//        }
//
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost("smtp.gmail.com");
//        mailSender.setPort(587);
//
//        mailSender.setUsername(prop.getProperty("spring.mail.username3"));
//        mailSender.setPassword(prop.getProperty("spring.mail.password3"));
//
//        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.transport.protocol", "smtp");
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.debug", "true");
//
//        return mailSender;
//    }
//
//    /**
//     * setting the message with setTo, setFrom, setText
//     *
//     * @return message
//     */
//    @Bean
//    public SimpleMailMessage emailTemplate() {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo("somebody@gmail.com");
//        message.setFrom("admin@gmail.com");
//        message.setText("FATAL - Application crash. Save your job !!");
//        return message;
//    }
//}
