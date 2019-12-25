package net.simforge.scenery.core;

import net.simforge.commons.hibernate.SessionFactoryBuilder;
import net.simforge.commons.misc.Str;
import net.simforge.refdata.aircraft.GeoRefData;
import net.simforge.scenery.core.persistence.Scenery;
import net.simforge.scenery.core.persistence.SceneryObject;
import net.simforge.scenery.core.persistence.SceneryRevision;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class LightWeightedScenery {

    public static final Class[] entities = {
            Scenery.class,
            SceneryRevision.class,
            SceneryObject.class
    };

    public static SessionFactory buildSessionFactory() {
        return SessionFactoryBuilder
                .forDatabase("lws")
                .entities(entities)
                .entities(GeoRefData.entities)
                .build();
    }

    public static SceneryRevision loadLastPublishedRevision(Session session, Scenery scenery) {
        //noinspection JpaQlInspection
        return (SceneryRevision) session
                .createQuery("from SceneryRevision where scenery = :scenery and status = :published order by revNumber desc")
                .setEntity("scenery", scenery)
                .setInteger("published", SceneryRevision.Status.Published)
                .setMaxResults(1)
                .uniqueResult();
    }

    public static List<SceneryObject> loadObjects(Session session, SceneryRevision revision) {
        //noinspection JpaQlInspection,unchecked
        return session
                .createQuery("from SceneryObject where revision = :revision")
                .setEntity("revision", revision)
                .list();
    }

    public static String revNumber(int revisionNumber) {
        return "rev" + Str.z(revisionNumber, 4);
    }
}
