package admin;


import org.apache.log4j.BasicConfigurator;
import org.example.thuchanh.data.Person;
import org.example.thuchanh.helper.XMLConvert;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Date;
import java.util.Properties;


public class QueueSender {
    public static void main(String[] args) throws Exception {

//config environment for JMS
        BasicConfigurator.configure();
//config environment for JNDI
        Properties settings = new Properties();
        settings.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
//create context
        Context ctx = new InitialContext(settings);
//lookup JMS connection factory
        ConnectionFactory factory =
                (ConnectionFactory) ctx.lookup("ConnectionFactory");
//lookup destination. (If not exist-->ActiveMQ create once)
        Destination destination =
                (Destination) ctx.lookup("dynamicQueues/tuananhdz");
//get connection using credential
        Connection con = factory.createConnection("admin", "admin");
//connect to MOM
        con.start();
//create session
        Session session = con.createSession(
                /*transaction*/false,
                /*ACK*/Session.AUTO_ACKNOWLEDGE
        );
//create producer
        MessageProducer producer = session.createProducer(destination);
//create text message
        Message msg = session.createTextMessage("hello mesage from ActiveMQ");
        producer.send(msg);
        Person p = new Person(1001, "tuấn anh dz", new Date());
        String xml = new XMLConvert<Person>(p).object2XML(p);
        msg = session.createTextMessage(xml);
        producer.send(msg);
//shutdown connection
        session.close();
        con.close();
        System.out.println("Finished...");
    }
}
