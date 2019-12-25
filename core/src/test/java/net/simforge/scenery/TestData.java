package net.simforge.scenery;

import net.simforge.scenery.core.Steps;
import net.simforge.scenery.core.persistence.Scenery;
import net.simforge.scenery.core.persistence.SceneryRevision;

import java.util.ArrayList;
import java.util.List;

public class TestData {

    public static final List<Scenery> sceneryList;

    public static final Scenery edli;
    public static final SceneryRevision edliRevision;
    public static final Scenery egsj;
    public static final SceneryRevision egsjRevision;
    public static final Scenery liml;
    public static final SceneryRevision limlRevision;
    public static final Scenery egll;
    public static final SceneryRevision egllRevision;

    public static final Scenery eddlTom = new Scenery();
    public static final SceneryRevision eddlTomRevision = new SceneryRevision();
    public static final Scenery lclk267151 = new Scenery();
    public static final SceneryRevision lclk267151Revision = new SceneryRevision();
    public static final Scenery lclkSS = new Scenery();
    public static final SceneryRevision lclkSSRevision = new SceneryRevision();
    public static final Scenery lcphMK = new Scenery();
    public static final SceneryRevision lcphMKRevision = new SceneryRevision();

    private static final String SMALL_SCENERY_TO_ADDON_SCENERY = "[{file: '" + Steps.PACKAGE + "', source: '" + Steps.SCENERY_TEXTURE + "', dest: '" + Steps.ADDON_SCENERY + "'}]";

    static {
        sceneryList = new ArrayList<>();

        edli = new Scenery();
        edli.setId(1);
        edli.setTitle("EDLI Bielefeld");
        edli.setAuthors("Alexey Kornev");
        sceneryList.add(edli);

        edliRevision = new SceneryRevision();
        edliRevision.setScenery(edli);
        edliRevision.setRevNumber(3);
        edliRevision.setRepoPath("EDLI");
        edliRevision.setRepoMode(SceneryRevision.RepoMode.Package);
        edliRevision.setInstallationSteps(SMALL_SCENERY_TO_ADDON_SCENERY);

        egsj = new Scenery();
        egsj.setId(2);
        egsj.setTitle("EGSJ Seething");
        egsj.setAuthors("Alexey Kornev");
        sceneryList.add(egsj);

        egsjRevision = new SceneryRevision();
        egsjRevision.setScenery(egsj);
        egsjRevision.setRevNumber(1);
        egsjRevision.setRepoPath("EGSJ");
        egsjRevision.setRepoMode(SceneryRevision.RepoMode.Package);
        egsjRevision.setInstallationSteps(SMALL_SCENERY_TO_ADDON_SCENERY);

        liml = new Scenery();
        liml.setId(3);
        liml.setTitle("LIML");
        liml.setAuthors("Alexey Kornev");
        sceneryList.add(liml);

        limlRevision = new SceneryRevision();
        limlRevision.setScenery(liml);
        limlRevision.setRevNumber(1);
        limlRevision.setRepoPath("LIML");
        limlRevision.setRepoMode(SceneryRevision.RepoMode.Package);
        limlRevision.setInstallationSteps(SMALL_SCENERY_TO_ADDON_SCENERY);

        egll = new Scenery();
        egll.setId(4);
        egll.setTitle("Heathrow Intl Airport - EGLL - UK by Ray Smith");
        egll.setAuthors("Ray Smith");
        sceneryList.add(egll);

        egllRevision = new SceneryRevision();
        egllRevision.setScenery(egll);
        egllRevision.setRevNumber(1);
        egllRevision.setRepoPath("EGLL");
        egllRevision.setRepoMode(SceneryRevision.RepoMode.Archives);
        egllRevision.setDestPath(Steps.OWN_FOLDER);
        egllRevision.setInstallationSteps("[{file: 'egll_ade_rs_fsx.zip', source: 'FSX_egll_ade_rs/*.bgl', dest: '%ADDON_SCENERY%/%REPO_PATH%/scenery'}]");

        fill(eddlTom, eddlTomRevision, (scenery, revision) -> {
            scenery.setId(5);
            scenery.setTitle("Duesseldorf scenery by Thomas Ruth");
            scenery.setAuthors("Thomas Ruth");
            sceneryList.add(scenery);

            revision.setScenery(scenery);
            revision.setRevNumber(1);
            revision.setRepoPath("EDDL-Tom");
            revision.setRepoMode(SceneryRevision.RepoMode.Archives);
            revision.setDestPath(Steps.OWN_FOLDER);
            revision.setInstallationSteps(Steps.newSteps()
                    .addStep("tomeddl_patchme_v1_0.zip", Steps.path("tom_eddl_ME", Steps.SCENERY_TEXTURE).toString(), Steps.OWN_FOLDER)
                    .addStep("tomeddl_updatedme_v1_01_patch.zip", Steps.path("tom_eddl_ME", Steps.SCENERY_TEXTURE).toString(), Steps.OWN_FOLDER)
                    .toJson());
        });

        fill(lclk267151, lclk267151Revision, (scenery, revision) -> {
            scenery.setId(6);
            scenery.setTitle("New Larnaca Int'l Airport");
            scenery.setAuthors("Tony Markides");
            sceneryList.add(scenery);

            revision.setScenery(scenery);
            revision.setRevNumber(1);
            revision.setRepoPath("LCLK_267151");
            revision.setRepoMode(SceneryRevision.RepoMode.Archives);
            revision.setDestPath(Steps.OWN_FOLDER);
            revision.setInstallationSteps(Steps.newSteps()
                    .addStep("lclk_267161.zip", Steps.path(Steps.SCENERY_TEXTURE).toString(), Steps.OWN_FOLDER)
                    .toJson());
        });

        fill(lclkSS, lclkSSRevision, (scenery, revision) -> {
            scenery.setId(7);
            scenery.setTitle("Larnaka LCLK, Cyprus, for FSX");
            scenery.setAuthors("Sidney Schwartz");
            sceneryList.add(scenery);

            revision.setScenery(scenery);
            revision.setRevNumber(1);
            revision.setRepoPath("LCLK_SidneySchwartz");
            revision.setRepoMode(SceneryRevision.RepoMode.Archives);
            revision.setDestPath(Steps.OWN_FOLDER);
            revision.setInstallationSteps(Steps.newSteps()
                    .addStep("larnaka_lclk.zip", Steps.path("Larnaca LCLK", Steps.SCENERY_TEXTURE).toString(), Steps.OWN_FOLDER)
                    .toJson());
        });

        fill(lcphMK, lcphMKRevision, (scenery, revision) -> {
            scenery.setId(8);
            scenery.setTitle("PAPHOS INTERNATIONAL AIRPORT - VERSION 2.00");
            scenery.setAuthors("Max Kraus");
            sceneryList.add(scenery);

            revision.setScenery(scenery);
            revision.setRevNumber(1);
            revision.setRepoPath("LCPH_MaxKraus");
            revision.setRepoMode(SceneryRevision.RepoMode.Archives);
            revision.setDestPath(Steps.OWN_FOLDER);
            revision.setInstallationSteps(Steps.newSteps()
                    .addStep("paphos_fsx_v2.zip", Steps.path("PAPHOS_FSX_v2", "PAPHOS FSXv2", Steps.SCENERY_TEXTURE).toString(), Steps.OWN_FOLDER)
                    .addStep("paphos_fsx_v2.zip", Steps.path("PAPHOS_FSX_v2", "Effects", "*").toString(), Steps.FSX_EFFECTS)
                    .addStep("paphos_fsx_v2-afcad_update.zip", "*.bgl", Steps.OWN_FOLDER + "/scenery")
                    .toJson());
        });
    }

    private static void fill(Scenery scenery, SceneryRevision revision, Filling filling) {
        filling.fill(scenery, revision);
    }

    public static String getTestDataRepo() {
        return "../core/test/repo";
    }

    private interface Filling {
        void fill(Scenery scenery, SceneryRevision revision);
    }
}
