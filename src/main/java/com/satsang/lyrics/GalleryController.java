package com.satsang.lyrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
// For file upload
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.io.IOException;

import java.util.List;

@RestController
@RequestMapping("/gallery")
@CrossOrigin(origins = "*")
public class GalleryController {

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
        @RequestParam Integer eventYear)
        throws IOException {

    String uploadDir =
            "src/main/resources/static/uploads/";

    Files.createDirectories(
            Paths.get(uploadDir));

    for (MultipartFile file : files) {

        String fileName =
                System.currentTimeMillis()
                + "_"
                + file.getOriginalFilename();

        Path path =
                Paths.get(uploadDir + fileName);

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
