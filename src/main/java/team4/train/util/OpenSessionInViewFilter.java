package team4.train.util;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import java.io.IOException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebFilter(urlPatterns = "/*")
public class OpenSessionInViewFilter extends HttpFilter implements Filter {
       
	private Session session;
	
    public OpenSessionInViewFilter() {
        super();
    }

    
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		
		
		try {
			SessionFactory factory = HibernateUtil.getSessionFactory();
			
			this.session = factory.getCurrentSession();
			
			session.beginTransaction();
			System.out.println("beginTransaction");
			
			chain.doFilter(request, response); //確保做完才commit
			
			session.getTransaction().commit();
			System.out.println("commit Transaction");
		} catch (Exception e) {
			session.getTransaction().rollback();
			System.out.println("rollback Transaction");
			e.printStackTrace();
		} 
		
	}


}
