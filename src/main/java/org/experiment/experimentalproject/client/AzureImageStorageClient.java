package org.experiment.experimentalproject.client;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class AzureImageStorageClient implements ImageStorageClient {
    private final BlobServiceClient blobServiceClient;

    public AzureImageStorageClient(BlobServiceClient blobServiceClient) {
        this.blobServiceClient = blobServiceClient;
    }

    @Override
    public String downloadImage(String containerName, String originalImageName, InputStream data, long length) throws IOException {
        BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);

        String newImageName = UUID.randomUUID().toString() + originalImageName.substring(originalImageName.lastIndexOf( "."));

        BlobClient blobClient = blobContainerClient.getBlobClient(newImageName);

        String contentType = Files.probeContentType(Path.of(originalImageName));

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(contentType);

        blobClient.upload(data, length, true);
        blobClient.setHttpHeaders(headers);

        return blobClient.getBlobUrl();
    }
}
