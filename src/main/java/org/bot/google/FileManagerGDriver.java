package org.bot.google;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class FileManagerGDriver {
    private static final Logger logger = LoggerFactory.getLogger(FileManagerGDriver.class);
    private int MAX_FILES_SIZE = 1000;

    final Drive driveService;
    final GoogleCredentials credentials;

    public FileManagerGDriver(final String pathFile) throws IOException, GeneralSecurityException {
        credentials = GoogleCredentials
                .fromStream(new FileInputStream(pathFile))
                .createScoped(Collections.singleton(DriveScopes.DRIVE));

        driveService = new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("DriveApp")
                .build();
    }

    public List<File> getFiles() throws IOException {
        FileList folder = getDriveService().files().list()
                .setQ("sharedWithMe=true or 'me' in owners")
                .setPageSize(10)
                .setFields("nextPageToken, files(id, name, mimeType, owners)")
                .execute();
        final String idFolder = folder.getFiles().get(0).getId();


        return getDriveService().files().list()
                .setQ("'" + idFolder + "' in parents") // Ищем файлы внутри папки
                .setPageSize(MAX_FILES_SIZE) // Максимальный размер выборки
                .setFields("nextPageToken, files(id, name, mimeType, size, modifiedTime)")
                .execute()
                .getFiles();
    }

    public byte[] downloadFileAsBytes(String fileId) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             InputStream in = getDriveService().files()
                     .get(fileId)
                     .executeMediaAsInputStream()) {

            byte[] buffer = new byte[1024 * 8]; // 8KB буфер
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return outputStream.toByteArray();
        }
    }

    public Drive getDriveService() {
        // Проверяем и обновляем токен при каждом запросе
        try {
            credentials.refreshIfExpired();
        } catch (final IOException e) {
            logger.error("error in refresh token:", e);
            throw new RuntimeException(e);
        }
        return driveService;
    }
}
