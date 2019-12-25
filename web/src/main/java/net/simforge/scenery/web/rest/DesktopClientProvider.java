package net.simforge.scenery.web.rest;

import net.simforge.commons.legacy.BM;
import net.simforge.commons.misc.RestUtils;
import net.simforge.scenery.core.dto.SceneryInfoDto;
import net.simforge.scenery.core.persistence.Scenery;
import net.simforge.scenery.core.persistence.SceneryRevision;
import net.simforge.scenery.core.service.PersistenceService;
import net.simforge.scenery.core.service.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/desktop")
public class DesktopClientProvider {

    private static final Logger logger = LoggerFactory.getLogger(DesktopClientProvider.class.getName());

    @Context
    private PersistenceService persistenceService;
    @Context
    private RepositoryService repositoryService;

    @GET
    @Path("/check-version")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkVersion() {
        Map<String, Object> result = new HashMap<>();
        return Response.ok(RestUtils.success(result)).build();
    }

    @GET
    @Path("/scenery-list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSceneryList() {
        BM.start("DesktopClientProvider.getSceneryList");
        try {
            List<Scenery> sceneryList = persistenceService.loadVisibleSceneryList();

            List<SceneryInfoDto> result = new ArrayList<>();
            for (Scenery scenery : sceneryList) {
                SceneryInfoDto dto = SceneryInfoDto.toDto(scenery, persistenceService.loadLastPublishedRevision(scenery));

                result.add(dto);
            }

            return Response.ok(RestUtils.success(result)).build();
        } catch (Throwable t) {
            logger.error("Server error happened", t);
            return Response.ok(RestUtils.failure("Server error happened")).build();
        } finally {
            BM.stop();
        }
    }

    @GET
    @Path("/download-package")
    public Response downloadPackage(@QueryParam("sceneryId") String _sceneryId) {
        BM.start("DesktopClientProvider.downloadPackage");
        try {
            int sceneryId = Integer.parseInt(_sceneryId);

            Scenery scenery = persistenceService.loadScenery(sceneryId);
            if (scenery == null) {
                return Response.status(404, "Can't find scenery").build();
            }

            SceneryRevision revision = persistenceService.loadLastPublishedRevision(scenery);

            // check if the scenery is exploded in the repository
            boolean packageMode = revision.getRepoMode().equals(SceneryRevision.RepoMode.Package);

            if (!packageMode) {
                return Response.status(404, "The scenery is not in package mode").build();
            }

            StreamingOutput fileStream = outputStream -> {
                repositoryService.loadPackage(revision, outputStream);

                outputStream.flush();
            };

            return Response
                    .ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
                    .header("content-disposition", "attachment; filename = package.zip")
                    .build();
        } catch (Throwable t) {
            logger.error("Server error happened", t);
            return Response.status(500, "Server error happened").build();
        } finally {
            BM.stop();
        }
    }

    @GET
    @Path("/download-archive")
    public Response downloadPackage(@QueryParam("sceneryId") String _sceneryId, @QueryParam("archiveName") String _archiveName) {
        BM.start("DesktopClientProvider.downloadArchive");
        try {
            int sceneryId = Integer.parseInt(_sceneryId);

            Scenery scenery = persistenceService.loadScenery(sceneryId);
            if (scenery == null) {
                return Response.status(404, "Can't find scenery").build();
            }

            SceneryRevision revision = persistenceService.loadLastPublishedRevision(scenery);

            // check if the scenery is exploded in the repository
            boolean archiveMode = revision.getRepoMode().equals(SceneryRevision.RepoMode.Archives);

            if (!archiveMode) {
                return Response.status(404, "The scenery is not in archive mode").build();
            }

            StreamingOutput fileStream = outputStream -> {
                repositoryService.loadArchive(revision, _archiveName, outputStream);

                outputStream.flush();
            };

            return Response
                    .ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
                    .header("content-disposition", "attachment; filename = package.zip")
                    .build();
        } catch (Throwable t) {
            logger.error("Server error happened", t);
            return Response.status(500, "Server error happened").build();
        } finally {
            BM.stop();
        }
    }
}
