package com.satsang.lyrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
// For file upload
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.io.IOException;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/gallery")
@CrossOrigin(origins = "${app.cors.allowed-origin:http://127.0.0.1:5500}")
public class GalleryController {

    // Allow only image types, and cap size to avoid arbitrary/huge uploads.
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );
    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024; // 10MB

    // External, configurable upload directory — NOT inside src/main/resources,
    // so uploaded files survive rebuilds/redeploys instead of being wiped.
    @Value("${app.upload.dir:${user.home}/satsang-uploads/}")
    private String uploadDir;

    @Autowired
    private GalleryRepository galleryRepository;

    // Get all gallery images
    @GetMapping("/all")
    public List<Gallery> getAllImages() {
        return galleryRepository.findAll();
    }


    @GetMapping("/{eventType}/{eventYear}")
public List<Gallery> getGalleryByEvent(
        @PathVariable String eventType,
        @PathVariable Integer eventYear) {

    return galleryRepository.findByEventTypeAndEventYear(
            eventType,
            eventYear
    );
}

    // Add image record
    @PostMapping("/add")
    public Gallery addImage(@RequestBody Gallery gallery) {
        return galleryRepository.save(gallery);
    }

    @PostMapping("/upload")
public String uploadGallery(
        @RequestParam("files") MultipartFile[] files,
        @RequestParam String eventType,
        @RequestParam Integer eventYear,
        HttpSession session)
        throws IOException {

    // This write endpoint should require admin auth, same as the lyrics ones.
    if (session.getAttribute("admin") == null) {
        return "UNAUTHORIZED";
    }

    Files.createDirectories(Paths.get(uploadDir));

    for (MultipartFile file : files) {

        if (file.isEmpty()) {
            continue;
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            return "REJECTED: unsupported file type (" + file.getOriginalFilename() + ")";
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            return "REJECTED: file too large (" + file.getOriginalFilename() + ")";
        }

        // Sanitize the original filename so path traversal (e.g. "../../x")
        // and unsafe characters can't reach the filesystem.
        String safeOriginalName = Paths.get(file.getOriginalFilename())
                .getFileName()
                .toString()
                .replaceAll("[^a-zA-Z0-9._-]", "_");

        String fileName =
                System.currentTimeMillis()
                + "_"
                + safeOriginalName;

        Path path = Paths.get(uploadDir, fileName);

        Files.copy(
                file.getInputStream(),
                path,
                StandardCopyOption.REPLACE_EXISTING);

        Gallery gallery =
                new Gallery();

        gallery.setEventType(eventType);
        gallery.setEventYear(eventYear);

        gallery.setImageUrl(
                "/uploads/" + fileName);

        galleryRepository.save(gallery);
    }

    return "Photos Uploaded Successfully";
}
}
