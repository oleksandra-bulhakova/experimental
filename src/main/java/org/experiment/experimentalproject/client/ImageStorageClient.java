package org.experiment.experimentalproject.client;

import java.io.IOException;
import java.io.InputStream;

public interface ImageStorageClient {
    String downloadImage(String containerName, String originalImageName, InputStream data, long length) throws IOException;
}
