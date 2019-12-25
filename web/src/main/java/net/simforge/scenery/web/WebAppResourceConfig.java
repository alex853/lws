package net.simforge.scenery.web;

import net.simforge.commons.legacy.misc.Settings;
import net.simforge.scenery.core.service.HibernatePersistenceService;
import net.simforge.scenery.core.service.PersistenceService;
import net.simforge.scenery.core.service.RepositoryService;
import net.simforge.scenery.core.service.SimpleRepositoryService;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ApplicationPath;
import java.util.function.Supplier;

@ApplicationPath("service")
public class WebAppResourceConfig extends ResourceConfig {
    public WebAppResourceConfig() {
        register(JacksonFeature.class);

        packages("net.simforge.scenery.web.rest");

        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(PersistenceServiceFactory.class).to(PersistenceService.class);
                bindFactory(RepositoryServiceFactory.class).to(RepositoryService.class);
            }
        });
    }

    public static class PersistenceServiceFactory implements Supplier<PersistenceService> {

        private final HttpServletRequest request;

        @Inject
        public PersistenceServiceFactory(Provider<HttpServletRequest> requestProvider) {
            this.request = requestProvider.get();
        }

        @Override
        public PersistenceService get() {
            ServletContext servletContext = request.getSession().getServletContext();
            WebAppContext webAppContext = WebAppContext.get(servletContext);
            return new HibernatePersistenceService(webAppContext.getSessionFactory());
        }
    }

    public static class RepositoryServiceFactory implements Supplier<RepositoryService> {
        @Override
        public RepositoryService get() {
            String repoPath = Settings.get("improved-scenery.repo-path");
            if (repoPath == null) {
                throw new IllegalStateException("Unable to work without repo-path configured");
            }
            return new SimpleRepositoryService(repoPath);
        }
    }
}
