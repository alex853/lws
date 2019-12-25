package net.simforge.scenery.core;

import net.simforge.commons.hibernate.HibernateUtils;
import net.simforge.refdata.aircraft.GeoRefData;
import net.simforge.refdata.aircraft.model.geo.Airport;
import net.simforge.scenery.core.persistence.Scenery;
import net.simforge.scenery.core.persistence.SceneryObject;
import net.simforge.scenery.core.persistence.SceneryRevision;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

public class AddTestData {
    public static void main(String[] args) {
        try (SessionFactory sessionFactory = LightWeightedScenery.buildSessionFactory();
             Session session = sessionFactory.openSession()) {

            HibernateUtils.transaction(session, () -> {
                //noinspection JpaQlInspection
                session.createQuery("delete from SceneryObject").executeUpdate();
                //noinspection JpaQlInspection
                session.createQuery("delete from SceneryRevision").executeUpdate();
                //noinspection JpaQlInspection
                session.createQuery("delete from Scenery").executeUpdate();
            });

            Scenery edli = createScenery(
                    session,
                    "EDLI Bielefeld",
                    "Layout corrections according to satellite images",
                    "Alexey Kornev");
            SceneryRevision revision = loadInProgressRevision(session, edli);
            revision = updateRevision(session, revision, "First corrections", "EDLI");
            addAirport(session, edli, "EDLI");
            publishRevision(session, revision);



            revision = addRevision(session, edli);
            revision = updateRevision(session, revision, "Grass runway added, taxiways corrected, some buildings done", "EDLI");
            publishRevision(session, revision);



            revision = addRevision(session, edli);
            revision = updateRevision(session, revision, "Some glider stuff added, few signs corrected", "EDLI");
            publishRevision(session, revision);





            Scenery egsj = createScenery(
                    session,
                    "EGSJ",
                    "Layout corrections according to satellite images",
                    "Alexey Kornev");
            revision = loadInProgressRevision(session, egsj);
            revision = updateRevision(session, revision, "First corrections", "EGSJ");
            addAirport(session, egsj, "EGSJ");
            publishRevision(session, revision);





            Scenery liml = createScenery(
                    session,
                    "LIML",
                    "Layout corrections according to satellite images",
                    "Alexey Kornev");
            revision = loadInProgressRevision(session, liml);
            revision = updateRevision(session, revision, "First attempt, nothing really new", "LIML");
            addAirport(session, liml, "LIML");
            publishRevision(session, revision);



        }
    }

    private static Scenery createScenery(Session session, String title, String description, String authors) {
        Scenery scenery = new Scenery();
        scenery.setTitle(title);
        scenery.setDescription(description);
        scenery.setAuthors(authors);

        SceneryRevision revision = new SceneryRevision();
        revision.setScenery(scenery);
        revision.setRevNumber(1);
        revision.setStatus(SceneryRevision.Status.InProgress);
        revision.setRepoMode(SceneryRevision.RepoMode.Package);

        HibernateUtils.saveAndCommit(session, scenery, revision);
        
        return scenery;
    }

    private static void addAirport(Session session, Scenery scenery, String icao) {
        SceneryRevision revision = loadInProgressRevision(session, scenery);

        Airport airport = GeoRefData.airportByIcao(session, icao);

        SceneryObject sObject = new SceneryObject();
        sObject.setRevision(revision);
        sObject.setType(SceneryObject.Type.Airport);
        sObject.setAirport(airport);

        HibernateUtils.saveAndCommit(session, sObject);
    }

    private static SceneryRevision loadInProgressRevision(Session session, Scenery scenery) {
        //noinspection JpaQlInspection
        return (SceneryRevision) session
                .createQuery("from SceneryRevision where scenery = :scenery and status = :inProgress")
                .setEntity("scenery", scenery)
                .setInteger("inProgress", SceneryRevision.Status.InProgress)
                .uniqueResult();
    }

    private static SceneryRevision updateRevision(Session session, SceneryRevision revision, String comment, String repoPath) {
        revision.setComment(comment);
        revision.setRepoPath(repoPath);

        HibernateUtils.saveAndCommit(session, revision);

        return revision;
    }

    private static void publishRevision(Session session, SceneryRevision revision) {
        revision.setStatus(SceneryRevision.Status.Published);

        HibernateUtils.saveAndCommit(session, revision);
    }

    private static SceneryRevision addRevision(Session session, Scenery scenery) {
        SceneryRevision inProgressRevision = loadInProgressRevision(session, scenery);

        if (inProgressRevision != null) {
            throw new IllegalStateException("There is InProgress revision, can't add new one");
        }

        SceneryRevision publishedRevision = LightWeightedScenery.loadLastPublishedRevision(session, scenery);
        if (publishedRevision == null) {
            throw new IllegalStateException("There is no Published revision");
        }

        SceneryRevision newRevision = new SceneryRevision();
        newRevision.setScenery(publishedRevision.getScenery());
        newRevision.setRevNumber(publishedRevision.getRevNumber() + 1);
        newRevision.setStatus(SceneryRevision.Status.InProgress);
        newRevision.setComment(publishedRevision.getComment());
        newRevision.setImages(publishedRevision.getImages());
        newRevision.setRepoPath(publishedRevision.getRepoPath());
        newRevision.setRepoMode(publishedRevision.getRepoMode());

        List<SceneryObject> sObjects = LightWeightedScenery.loadObjects(session, publishedRevision);

        List<SceneryObject> newSObjects = new ArrayList<>();
        for (SceneryObject sObject : sObjects) {
            SceneryObject newSObject = new SceneryObject();
            newSObject.setRevision(newRevision);
            newSObject.setLatitude(sObject.getLatitude());
            newSObject.setLongitude(sObject.getLongitude());
            newSObject.setType(sObject.getType());
            newSObject.setType(sObject.getType());
            newSObject.setAirport(sObject.getAirport());
            newSObject.setImages(sObject.getImages());
            newSObjects.add(newSObject);
        }

        List objects = new ArrayList<>();
        objects.add(newRevision);
        objects.addAll(newSObjects);

        HibernateUtils.saveAndCommit(session, objects.toArray());

        return newRevision;
    }

}
