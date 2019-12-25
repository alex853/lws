package net.simforge.scenery.web.rest;

import net.simforge.commons.misc.RestUtils;
import net.simforge.scenery.TestData;
import net.simforge.scenery.core.dto.SceneryInfoDto;
import net.simforge.scenery.core.service.PersistenceService;
import net.simforge.scenery.core.service.RepositoryService;
import net.simforge.scenery.core.service.SimpleRepositoryService;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DesktopClientProviderTest extends JerseyTest {

    private static PersistenceService persistenceServiceMock = Mockito.mock(PersistenceService.class);
    private static RepositoryService repositoryService = new SimpleRepositoryService(TestData.getTestDataRepo());

    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig();

        config.register(JacksonFeature.class);

        config.packages("net.simforge.scenery.web.rest");

        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(persistenceServiceMock).to(PersistenceService.class);
                bind(repositoryService).to(RepositoryService.class);
            }
        });
        return config;
    }

    @Before
    public void before() {
        Mockito.when(persistenceServiceMock.loadVisibleSceneryList()).thenReturn(TestData.sceneryList);

        Mockito.when(persistenceServiceMock.loadScenery(1)).thenReturn(TestData.edli);
        Mockito.when(persistenceServiceMock.loadLastPublishedRevision(TestData.edli)).thenReturn(TestData.edliRevision);

        Mockito.when(persistenceServiceMock.loadScenery(2)).thenReturn(TestData.egsj);
        Mockito.when(persistenceServiceMock.loadLastPublishedRevision(TestData.egsj)).thenReturn(TestData.egsjRevision);

        Mockito.when(persistenceServiceMock.loadScenery(3)).thenReturn(TestData.liml);
        Mockito.when(persistenceServiceMock.loadLastPublishedRevision(TestData.liml)).thenReturn(TestData.limlRevision);

        Mockito.when(persistenceServiceMock.loadScenery(4)).thenReturn(TestData.egll);
        Mockito.when(persistenceServiceMock.loadLastPublishedRevision(TestData.egll)).thenReturn(TestData.egllRevision);

        Mockito.when(persistenceServiceMock.loadScenery(5)).thenReturn(TestData.eddlTom);
        Mockito.when(persistenceServiceMock.loadLastPublishedRevision(TestData.eddlTom)).thenReturn(TestData.eddlTomRevision);

        Mockito.when(persistenceServiceMock.loadScenery(6)).thenReturn(TestData.lclk267151);
        Mockito.when(persistenceServiceMock.loadLastPublishedRevision(TestData.lclk267151)).thenReturn(TestData.lclk267151Revision);

        Mockito.when(persistenceServiceMock.loadScenery(7)).thenReturn(TestData.lclkSS);
        Mockito.when(persistenceServiceMock.loadLastPublishedRevision(TestData.lclkSS)).thenReturn(TestData.lclkSSRevision);

        Mockito.when(persistenceServiceMock.loadScenery(8)).thenReturn(TestData.lcphMK);
        Mockito.when(persistenceServiceMock.loadLastPublishedRevision(TestData.lcphMK)).thenReturn(TestData.lcphMKRevision);

        Mockito.when(persistenceServiceMock.loadScenery(9)).thenReturn(TestData.egph25A);
        Mockito.when(persistenceServiceMock.loadLastPublishedRevision(TestData.egph25A)).thenReturn(TestData.egph25ARevision);
    }

    @Test
    public void testSceneryList() throws IOException {
        String content = target("desktop/scenery-list").request().get(String.class);

        RestUtils.Response<SceneryInfoDto> response = RestUtils.parseResponse(content, SceneryInfoDto::readScenery);

        List<SceneryInfoDto> data = response.getData();

        assertEquals(9, data.size());

        SceneryInfoDto edli = data.get(0);
        assertEquals("1", edli.getId());
        assertEquals("EDLI Bielefeld", edli.getSceneryTitle());
        assertEquals("3", edli.getRevisionNumber());

        SceneryInfoDto egsj = data.get(1);
        assertEquals("2", egsj.getId());
        assertEquals("EGSJ Seething", egsj.getSceneryTitle());
        assertEquals("1", egsj.getRevisionNumber());

        SceneryInfoDto liml = data.get(2);
        assertEquals("3", liml.getId());
        assertEquals("LIML", liml.getSceneryTitle());
        assertEquals("1", liml.getRevisionNumber());

        SceneryInfoDto egll = data.get(3);
        assertEquals("4", egll.getId());
        assertEquals("Heathrow Intl Airport - EGLL - UK by Ray Smith", egll.getSceneryTitle());
        assertEquals("1", egll.getRevisionNumber());
    }

    @Test
    public void testDownloadPackage_ok() {
        // it loads EDLI which in package mode
        Response response = target("desktop/download-package").queryParam("sceneryId", TestData.edli.getId()).request().get();
        String contentType = (String) response.getHeaders().get("Content-Type").get(0);
        assertEquals("application/octet-stream", contentType);
    }

    @Test
    public void testDownloadPackage_failDueToArchiveMode() throws IOException {
        // it trie to load EGLL which in 'archives' mode and SHOULD fail
        Response response = target("desktop/download-package").queryParam("sceneryId", TestData.egll.getId()).request().get();
        assertNotEquals(200, response.getStatus());
    }
}
