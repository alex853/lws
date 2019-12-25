package net.simforge.scenery.core.service;

import net.simforge.scenery.core.persistence.Scenery;
import net.simforge.scenery.core.persistence.SceneryRevision;

import java.util.List;

public interface PersistenceService {

    Scenery loadScenery(int sceneryId);

    List<Scenery> loadVisibleSceneryList();

    SceneryRevision loadLastPublishedRevision(Scenery scenery);

}
