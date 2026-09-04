package com.satsang.lyrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "${app.cors.allowed-origin:http://127.0.0.1:5500}")
@RestController
@RequestMapping("/lyrics")
public class LyricsController {

    @Autowired
    private LyricsRepository lyricsRepository;

    @GetMapping("/")
    public String home() {
        return "Lyrics backend is running successfully 🚀";
    }

    @GetMapping("/all")
    public List<Lyrics> getAllLyrics() {
        return lyricsRepository.findAll();
    }

}
