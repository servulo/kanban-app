package com.kanbanapp.service;

import java.io.InputStream;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StorageService {

    private final BlobContainerClient containerClient;

    public StorageService(
        @ConfigProperty(name = "azure.storage.connection-string") String connectionString,
        @ConfigProperty(name = "azure.storage.container-name") String containerName
    ) {
        BlobServiceClient serviceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

                this.containerClient = serviceClient.getBlobContainerClient(containerName);

                if(!this.containerClient.exists()) {
                    this.containerClient.create();
                }
    }

    public String upload(String originalFileName, InputStream data, long size, String contentType) {
        String blobNanme = UUID.randomUUID() + "-" + originalFileName;
        BlobClient blobClient = containerClient.getBlobClient(blobNanme);
        blobClient.upload(data, size, true);
        return blobClient.getBlobUrl();
    }

    public void delete(String blobUrl) {
        String blobName = blobUrl.substring(blobUrl.lastIndexOf("/") + 1);
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        if(blobClient.exists()) {
            blobClient.delete();
        }
    }
    
}
