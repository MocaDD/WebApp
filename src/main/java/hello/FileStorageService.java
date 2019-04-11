package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)

            Path currentRelativePath = Paths.get("");
            String current_dir = currentRelativePath.toAbsolutePath().toString();
            Path filepath = Paths.get("");

            if (fileName.endsWith(".jar"))  {
                String filename = "uploads/jars/" + fileName;
                filepath = currentRelativePath.resolve(filename);
            } else  {
                String filename = "uploads/bin/" + fileName;
                filepath = currentRelativePath.resolve(filename);
            }

            try {
                Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

            } catch (Exception ie)  {

                File filePath = new File(fileName);
                filePath.delete();
            }
            return fileName;
        } catch (Exception ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
}