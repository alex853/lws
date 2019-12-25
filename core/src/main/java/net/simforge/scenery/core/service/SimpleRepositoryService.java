package net.simforge.scenery.core.service;

import net.simforge.scenery.core.ImprovedScenery;
import net.simforge.scenery.core.persistence.SceneryRevision;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SimpleRepositoryService implements RepositoryService {

    private String repoRoot;

    public SimpleRepositoryService(String repoRoot) {
        this.repoRoot = repoRoot;
    }

    @Override
    public void loadPackage(SceneryRevision revision, OutputStream outputStream) throws IOException {
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

        String location = repoRoot + "/" + revision.getRepoPath() + "/" + ImprovedScenery.revNumber(revision.getRevNumber());
        Path locationPath = Paths.get(location);
        List<String> files = new ArrayList<>();
        Files.walkFileTree(locationPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                files.add(locationPath.relativize(file).toString());
                return FileVisitResult.CONTINUE;
            }
        });

        for (String file : files) {
            ZipEntry ze = new ZipEntry(file);
            zipOutputStream.putNextEntry(ze);
            byte[] data = Files.readAllBytes(Paths.get(location, file));
            zipOutputStream.write(data);
            zipOutputStream.closeEntry();
        }

        zipOutputStream.close();
    }

    @Override
    public void loadArchive(SceneryRevision revision, String archiveName, OutputStream outputStream) throws IOException {
        if (archiveName.contains("\\")
                || archiveName.contains("/")
                || !archiveName.toLowerCase().endsWith(".zip")
                || archiveName.contains("~")
                || archiveName.contains("..")) {
            throw new IOException("Can't find archive by name '" + archiveName + "'");
        }

        String location = repoRoot + "/" + revision.getRepoPath();
        String filename = location + "/" + archiveName;

        byte[] data = Files.readAllBytes(Paths.get(filename));
        outputStream.write(data);
    }
}
