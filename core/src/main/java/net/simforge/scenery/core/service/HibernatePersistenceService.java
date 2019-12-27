package net.simforge.scenery.core.service;

import net.simforge.commons.legacy.BM;
import net.simforge.scenery.core.LightWeightedScenery;
import net.simforge.scenery.core.persistence.Scenery;
import net.simforge.scenery.core.persistence.SceneryRevision;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class HibernatePersistenceService implements PersistenceService {
    private SessionFactory sessionFactory;

    public HibernatePersistenceService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Scenery loadScenery(int sceneryId) {
        BM.start("HibernatePersistenceService.loadScenery");
        try (Session session = sessionFactory.openSession()) {

            return session.load(Scenery.class, sceneryId);

        } finally {
            BM.stop();
        }
    }

    @Override
    public List<Scenery> loadVisibleSceneryList() {
        BM.start("HibernatePersistenceService.loadVisibleSceneryList");
        try (Session session = sessionFactory.openSession()) {

            //noinspection unchecked,JpaQlInspection
            return session
                    .createQuery("from Scenery") // todo it fails when some scenery does not have published revision
                    .list();

        } finally {
            BM.stop();
        }
    }

    @Override
    public SceneryRevision loadLastPublishedRevision(Scenery scenery) {
        BM.start("HibernatePersistenceService.loadLastPublishedRevision");
        try (Session session = sessionFactory.openSession()) {

            return LightWeightedScenery.loadLastPublishedRevision(session, scenery);

        } finally {
            BM.stop();
        }
    }
}
