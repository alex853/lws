package net.simforge.scenery.desktop;

import net.simforge.commons.io.IOHelper;
import net.simforge.commons.legacy.BM;
import net.simforge.commons.misc.RestUtils;
import net.simforge.scenery.core.dto.SceneryInfoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class RestClient {

    private static final Logger logger = LoggerFactory.getLogger(RestClient.class.getName());

    private Configuration cfg;

    public RestClient(Configuration cfg) {
        this.cfg = cfg;
    }

    public List<SceneryInfoDto> loadSceneryList() throws IOException {
        BM.start("RestClient.loadSceneryList");
        try {
            String url = cfg.getDesktopClientServiceUrl() + "/scenery-list";
            logger.debug("Loading scenery list using URL {}", url);

            String content = IOHelper.download(url);

            RestUtils.Response<SceneryInfoDto> response = RestUtils.parseResponse(content, SceneryInfoDto::readScenery);

            if (response.isSuccess()) {
                return response.getData();
            } else {
                throw new IllegalStateException("Error on load scenery data, message is '" + response.getMessage() + "'");
            }
        } finally {
            BM.stop();
        }
    }

    public void downloadPackage(SceneryInfoDto scenery, String destFilename) throws IOException {
        BM.start("RestClient.downloadPackage");
        try {
            String downloadUrl = cfg.getDesktopClientServiceUrl() + "/download-package?sceneryId=" + scenery.getId();
            logger.debug("Loading package using URL {}", downloadUrl);

            URL url = new URL(downloadUrl);
            try (InputStream is = url.openStream();
                 FileOutputStream fos = new FileOutputStream(destFilename)) {
                IOHelper.copyStream(is, fos);
            }
        } finally {
            BM.stop();
        }
    }

    public void downloadArchive(SceneryInfoDto scenery, String archiveName, String destFilename) throws IOException {
        BM.start("RestClient.downloadArchive");
        try {
            String downloadUrl = cfg.getDesktopClientServiceUrl() + "/download-archive?sceneryId=" + scenery.getId() + "&archiveName=" + archiveName;
            logger.debug("Loading archive {} using URL {}", archiveName, downloadUrl);

            URL url = new URL(downloadUrl);
            try (InputStream is = url.openStream();
                 FileOutputStream fos = new FileOutputStream(destFilename)) {
                IOHelper.copyStream(is, fos);
            }
        } finally {
            BM.stop();
        }
    }
}
