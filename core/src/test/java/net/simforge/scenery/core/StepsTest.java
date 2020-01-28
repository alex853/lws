package net.simforge.scenery.core;

import org.junit.Test;

import static net.simforge.scenery.core.Steps.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StepsTest {

    @Test
    public void testSource_SCENERY_TEXTURE() {
        assertTrue(Source.isMatch(SCENERY_TEXTURE, "scenery/layout.bgl"));
        assertEquals("scenery/layout.bgl", Source.getMatchedFilename(SCENERY_TEXTURE, "scenery/layout.bgl"));
        assertTrue(Source.isMatch(SCENERY_TEXTURE, "scenery/somefile.txt"));
        assertTrue(Source.isMatch(SCENERY_TEXTURE, "texture/image.bmp"));
        assertFalse(Source.isMatch(SCENERY_TEXTURE, "images/layout.bgl"));
    }

    @Test
    public void testSource_somepath_then_SCENERY_TEXTURE() {
        assertTrue(Source.isMatch("somepath/" + SCENERY_TEXTURE, "somepath/scenery/layout.bgl"));
        assertEquals("scenery/layout.bgl", Source.getMatchedFilename("somepath/" + SCENERY_TEXTURE, "somepath/scenery/layout.bgl"));
        assertTrue(Source.isMatch("somepath/" + SCENERY_TEXTURE, "somepath/scenery/somefile.txt"));
        assertTrue(Source.isMatch("somepath/" + SCENERY_TEXTURE, "somepath/texture/image.bmp"));
        assertFalse(Source.isMatch("somepath/" + SCENERY_TEXTURE, "somepath/images/layout.bgl"));
        assertFalse(Source.isMatch("somepath/" + SCENERY_TEXTURE, "somepath/somefile.txt"));
        assertFalse(Source.isMatch("somepath/" + SCENERY_TEXTURE, "scenery/somefile.bgl"));
        assertFalse(Source.isMatch("somepath/" + SCENERY_TEXTURE, "somefile.txt"));
    }

    @Test
    public void testSource_simpleMask() {
        assertTrue(Source.isMatch("*.bgl", "layout.bgl"));
        assertEquals("layout.bgl", Source.getMatchedFilename("*.bgl", "layout.bgl"));
        assertFalse(Source.isMatch("*.bgl", "scenery/layout.bgl"));
        assertFalse(Source.isMatch("*.bgl", "layout.txt"));
    }

    @Test
    public void testSource_maskWithPath() {
        assertFalse(Source.isMatch("somepath/*", "layout.bgl"));
        assertTrue(Source.isMatch("somepath/*", "somepath/layout.bgl"));
        assertEquals("layout.bgl", Source.getMatchedFilename("somepath/*", "somepath/layout.bgl"));
        assertTrue(Source.isMatch("somepath/*", "somepath/layout.txt"));
        assertFalse(Source.isMatch("somepath/*", "somepath/another/layout.txt"));
    }

    @Test
    public void testSource_maskWithPathAndExtension() {
        assertTrue(Source.isMatch("somepath/*.bgl", "somepath/layout.bgl"));
        assertEquals("layout.bgl", Source.getMatchedFilename("somepath/*.bgl", "somepath/layout.bgl"));
        assertFalse(Source.isMatch("somepath/*.bgl", "somepath/image.jpg"));
        assertTrue(Source.isMatch("somepath/*.bgl", "SOMEPATH/layout.BGL"));
    }

    @Test
    public void testSource_arrayOfBoundsException() {
        assertFalse(Source.isMatch("somepath/*.bgl", "somepath/"));
    }

    @Test
    public void testSource_edliError() {
        assertTrue(Source.isMatch(SCENERY_TEXTURE, "scenery\\EDLI-1710.bgl"));
        assertEquals("scenery/EDLI-1710.bgl", Source.getMatchedFilename(SCENERY_TEXTURE, "scenery\\EDLI-1710.bgl"));
    }

    @Test
    public void testSource_sameExactFilenames_lowwError() {
        assertTrue(Source.isMatch("FSX_loww_rs/LOWW_RS.BGL", "FSX_loww_rs/LOWW_RS.BGL"));
        assertEquals("LOWW_RS.BGL", Source.getMatchedFilename("FSX_loww_rs/LOWW_RS.BGL", "FSX_loww_rs/LOWW_RS.BGL"));
    }

    private void probe() {

        // EDLI, ...
        newStep(PACKAGE, SCENERY_TEXTURE, ADDON_SCENERY);

        // EGLL
        newStep("egll_ade_rs_fsx.zip", "FSX_egll_ade_rs/*.bgl", "%ADDON_SCENERY%/%REPO_PATH%/scenery");

        //EDLL-Tom
        newStep("tomeddl_patchme_v1_0.zip", "tom_eddl_ME/%SCENERY&TEXTURE%", "%ADDON_SCENERY%/%REPO_PATH%");
        newStep("tomeddl_updatedme_v1_01_patch.zip", "tom_eddl_ME/%SCENERY&TEXTURE%", "%ADDON_SCENERY%/%REPO_PATH%");

        // LCPH
        newStep("paphos_fsx_v2.zip", "PAPHOS_FSX_v2/PAPHOS FSXv2/%SCENERY&TEXTURE%", "%ADDON_SCENERY%/%REPO_PATH%");
        newStep("paphos_fsx_v2.zip", "PAPHOS_FSX_v2/Effects/*", "%FSX_EFFECTS%");
        newStep("paphos_fsx_v2-afcad_update.zip", "*.bgl", "%ADDON_SCENERY%/%REPO_PATH%/scenery");

    }

    private void newStep(String file, String source, String dest) {

    }
}
