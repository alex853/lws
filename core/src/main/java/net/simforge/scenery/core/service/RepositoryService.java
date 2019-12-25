package net.simforge.scenery.core.service;

import net.simforge.scenery.core.persistence.SceneryRevision;

import java.io.IOException;
import java.io.OutputStream;

public interface RepositoryService {

    void loadPackage(SceneryRevision revision, OutputStream outputStream) throws IOException;

    void loadArchive(SceneryRevision revision, String archiveName, OutputStream outputStream) throws IOException;

}
