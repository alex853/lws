package net.simforge.scenery.core.service;

import net.simforge.scenery.TestData;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SimpleRepositoryServiceTest {

    private RepositoryService repositoryService;

    @Before
    public void before() {
        repositoryService = new SimpleRepositoryService(TestData.getTestDataRepo());
    }

    @Test
    public void testLoadPackage_ok() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        repositoryService.loadPackage(TestData.edliRevision, baos);
        baos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        List<String> files = new ArrayList<>();
        ZipInputStream zipInputStream = new ZipInputStream(bais);
        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            String file = entry.getName();
            files.add(file.replace('/', '\\'));
        }

        assertEquals(3, files.size());
        assertTrue(files.contains("scenery\\EDLI-1710.bgl"));
        assertTrue(files.contains("scenery\\EDLI-1710_CVX.bgl"));
        assertTrue(files.contains("scenery\\EDLI-1710_OBJ.bgl"));
    }

    @Test(expected = IOException.class)
    public void testLoadPackage_failure() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        repositoryService.loadPackage(TestData.egllRevision, baos);
        baos.close();
    }

    @Test
    public void testLoadArchive_ok() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        repositoryService.loadArchive(TestData.egllRevision, "egll_ade_rs_fsx.zip", baos);
        baos.close();

        byte[] bytes = baos.toByteArray();
        assertEquals(1168682, bytes.length);
    }

    @Test(expected = IOException.class)
    public void testLoadArchive_wrongName() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        repositoryService.loadArchive(TestData.egllRevision, "egll_ade_rs_fsx_v2.zip", baos);
        baos.close();
    }

    @Test(expected = IOException.class)
    public void testLoadArchive_packageSceneryLoadingAttempt() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        repositoryService.loadArchive(TestData.egllRevision, "rev0001\\scenery\\EDLI-1710.bgl", baos);
        baos.close();
    }

    @Test(expected = IOException.class)
    public void testLoadArchive_hackAttempt1() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        repositoryService.loadArchive(TestData.egllRevision, "../../../somefile.zip", baos);
        baos.close();
    }
}
