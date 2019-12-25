package net.simforge.scenery.desktop;

import net.simforge.commons.io.IOHelper;
import net.simforge.scenery.desktop.model.SceneryCfg;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SceneryCfgTest {
    @Test
    public void testLoadingOfFsxOriginal() throws IOException {
        SceneryCfg sceneryCfg = SceneryCfg.load(IOHelper.resourceToPath(SceneryCfgTest.class, "scenery.fsx-original.cfg"));

        assertEquals("FS9 World Scenery", sceneryCfg.getTitle());

        assertEquals(120, sceneryCfg.getAreas().size());

        SceneryCfg.Area area119 = sceneryCfg.getAreaByNumber(119);
        assertNotNull(area119);
        assertEquals("Edwards_AFB", area119.getTitle());
        assertEquals("Scenery\\Cities\\Edwards_AFB", area119.getLocal());
        assertEquals(119, area119.getLayer());
        assertTrue(area119.isActive());
        assertTrue(area119.isRequired());
    }

    @Test
    public void testAddScenery() throws IOException {
        SceneryCfg sceneryCfg = SceneryCfg.load(IOHelper.resourceToPath(SceneryCfgTest.class, "scenery.fsx-original.cfg"));

        SceneryCfg.Area newArea = sceneryCfg.append();
        newArea.setTitle("some title");
        newArea.setLocal("Addon Scenery\\some path");
        newArea.setActive(true);
        newArea.setRequired(false);

        File tmpFile = new File("./temp/scenery." + System.currentTimeMillis() + ".cfg");
        tmpFile.getParentFile().mkdirs();

        sceneryCfg.save(tmpFile.getAbsolutePath());

        sceneryCfg = SceneryCfg.load(tmpFile.getAbsolutePath());
        assertEquals(121, sceneryCfg.getAreas().size());
        assertEquals(newArea.getTitle(), sceneryCfg.getAreaByNumber(121).getTitle());
        assertEquals(newArea.getLocal(), sceneryCfg.getAreaByNumber(121).getLocal());
        assertEquals(newArea.isActive(), sceneryCfg.getAreaByNumber(121).isActive());
        assertEquals(newArea.isRequired(), sceneryCfg.getAreaByNumber(121).isRequired());

        tmpFile.deleteOnExit();
    }

    @Test
    public void testDeleteEGKK() throws IOException {
        SceneryCfg sceneryCfg = SceneryCfg.load(IOHelper.resourceToPath(SceneryCfgTest.class, "Scenery.fsx-orbx.CFG"));

        SceneryCfg.Area area = sceneryCfg.getAreaByLocal("Addon Scenery\\EGKK_25A");
        sceneryCfg.remove(area);

        File tmpFile = new File("./temp/scenery." + System.currentTimeMillis() + ".cfg");
        tmpFile.getParentFile().mkdirs();

        sceneryCfg.save(tmpFile.getAbsolutePath());

        sceneryCfg = SceneryCfg.load(tmpFile.getAbsolutePath());
        assertEquals(131, sceneryCfg.getAreas().size());
        assertEquals("Paphos FSX v2", sceneryCfg.getAreaByNumber(130).getTitle());
        assertEquals("FTXAA_ORBXLIBS", sceneryCfg.getAreaByNumber(131).getTitle());

        tmpFile.deleteOnExit();
    }
}
