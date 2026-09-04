package com.satsang.lyrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/admin")
@CrossOrigin(
    origins = "${app.cors.allowed-origin:http://127.0.0.1:5500}",
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
    // POST with a JSON body instead of GET+query params, so the password
    // never appears in the URL, server access logs, or browser history.
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request, HttpSession session) {

        Admin admin = adminRepository.findByUsername(request.getUsername());

        if (admin == null) {
            return "USER_NOT_FOUND";
        }

        boolean match = passwordEncoder.matches(request.getPassword(), admin.getPassword());

        if (match) {
            session.setAttribute("admin", admin.getUsername());
            return "LOGIN_SUCCESS";
        } else {
            return "WRONG_PASSWORD";
        }
    }

    // Simple DTO so the password travels in the request body, not the URL.
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
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
        @RequestParam String youtubeLink,
        HttpSession session) {

    if (session.getAttribute("admin") == null) {
        return "UNAUTHORIZED";
    }

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

