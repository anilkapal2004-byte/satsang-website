package com.satsang.lyrics;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LyricsRepository extends JpaRepository<Lyrics, Long> {
    
    Lyrics findByTitleIgnoreCase(String title);
}
