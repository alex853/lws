package net.simforge.scenery.web;

import net.simforge.scenery.core.LightWeightedScenery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;

public class WebAppContext {

    private static final Logger logger = LoggerFactory.getLogger(WebAppContext.class.getName());
    private static final String ATTRIBUTE_NAME = WebAppContext.class.getName();

    private SessionFactory sessionFactory;

    protected static void create(ServletContext servletContext) {
        servletContext.setAttribute(ATTRIBUTE_NAME, new WebAppContext());
    }

    public static WebAppContext get(ServletContext servletContext) {
        return (WebAppContext) servletContext.getAttribute(ATTRIBUTE_NAME);
    }

    private WebAppContext() {
        logger.info("creating session factory");

        sessionFactory = LightWeightedScenery.buildSessionFactory();

        logger.info("session factory has been built");
    }

    public synchronized void destroy() {
        logger.info("closing session manager");

        sessionFactory.close();
        sessionFactory = null;

        logger.info("session manager is closed");
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public Session openSession() {
        return sessionFactory.openSession();
    }
}
