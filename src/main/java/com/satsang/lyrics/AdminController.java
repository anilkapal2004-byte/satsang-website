package com.satsang.lyrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
@RequestMapping("/admin")
@CrossOrigin(
    origins = "http://127.0.0.1:5500",
    allowCredentials = "true"
)
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private LyricsRepository lyricsRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    

    // ================= LOGIN =================
    @GetMapping("/login")
public String login(@RequestParam String username,
                    @RequestParam String password,
                    HttpSession session) {

    Admin admin = adminRepository.findByUsername(username);

    if (admin == null) {
        return "USER_NOT_FOUND";
    }

    System.out.println("RAW PASSWORD FROM DB = " + admin.getPassword());
    System.out.println("PASSWORD ENTERED = " + password);

    boolean match = passwordEncoder.matches(password, admin.getPassword());
    System.out.println("MATCH RESULT = " + match);

    if (match) {
        session.setAttribute("admin", admin.getUsername());
        return "LOGIN_SUCCESS";
    } else {
        return "WRONG_PASSWORD";
    }
}


@PostMapping("/addBhajan")
public String addBhajan(@RequestBody Lyrics lyrics, HttpSession session) {

    if (session.getAttribute("admin") == null) {
        return "UNAUTHORIZED";
    }

    lyricsRepository.save(lyrics);
    return "BHAJAN_ADDED_SUCCESSFULLY";
}

@PutMapping("/editBhajan/{id}")
public String editBhajan(@PathVariable Long id,
                         @RequestBody Lyrics updated,
                         HttpSession session) {

    if (session.getAttribute("admin") == null) {
        return "UNAUTHORIZED";
    }

    Lyrics existing = lyricsRepository.findById(id).orElse(null);
    if (existing == null) return "NOT_FOUND";

    existing.setTitle(updated.getTitle());
    existing.setLyrics(updated.getLyrics());
    existing.setLanguage(updated.getLanguage());

    lyricsRepository.save(existing);
    return "BHAJAN_UPDATED";
}

// ================= YOUTUBE LINK =================
@PutMapping("/addYoutube")
public String addYoutubeLink(
        @RequestParam String title,
        @RequestParam String youtubeLink) {

    Lyrics bhajan =
    lyricsRepository.findByTitleIgnoreCase(title);

    if (bhajan == null) {
        return "BHAJAN_NOT_FOUND";
    }

    bhajan.setYoutubeLink(youtubeLink);

    lyricsRepository.save(bhajan);

    return "YOUTUBE_LINK_ADDED_SUCCESSFULLY";
}







}

