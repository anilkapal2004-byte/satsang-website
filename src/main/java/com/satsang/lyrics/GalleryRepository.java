package com.satsang.lyrics;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GalleryRepository
        extends JpaRepository<Gallery, Long> {

    List<Gallery> findByEventTypeAndEventYear(
            String eventType,
            Integer eventYear
    );
}