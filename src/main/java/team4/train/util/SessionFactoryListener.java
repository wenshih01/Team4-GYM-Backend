package team4.train.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;


@WebListener
public class SessionFactoryListener implements ServletContextListener {

public SessionFactoryListener() {
        
    }


 
    public void contextInitialized(ServletContextEvent sce)  { 
       HibernateUtil.getSessionFactory();
       System.out.println("SessionFactory Created!!!");
    }


 
    public void contextDestroyed(ServletContextEvent sce)  { 
      HibernateUtil.closeSessionFactory();
      System.out.println("SessionFactory closed!!");
    }

 
}
