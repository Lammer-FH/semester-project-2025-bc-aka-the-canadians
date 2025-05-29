package com.campuslostfound.config;

import com.campuslostfound.model.*;
import com.campuslostfound.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final ItemRepository itemRepository;
    private final ReportRepository reportRepository;

    @PostConstruct
    public void init() {
        // Users
        User alice = userRepository.save(new User(null, "Alice", "alice@example.com", "123456789"));
        User bob = userRepository.save(new User(null, "Bob", "bob@example.com", "987654321"));
        User charlie = userRepository.save(new User(null, "Charlie", "charlie@example.com", "111222333"));

        // Locations
        Location library = locationRepository.save(new Location(null, "Library Entrance", "Main library help desk area", "Library", "1", "L101"));
        Location cafeteria = locationRepository.save(new Location(null, "Cafeteria South", "Corner by the recycling bins", "Cafeteria", "G", "C005"));
        Location lab = locationRepository.save(new Location(null, "Physics Lab", "Near equipment room", "Science Building", "2", "S204"));

        // Report 1 (Alice): 1 item
        Report report1 = new Report(null, "Lost Backpack", "Black CS backpack lost in library.",
                LocalDateTime.now().minusDays(3), ReportType.LOST_ITEM, ReportStatus.OPEN, alice, null);
        report1 = reportRepository.save(report1);

        Item item1 = new Item(null, "Backpack", "Black backpack with CS logo", "Backpack", "https://example.com/backpack.jpg",
                LocalDateTime.now().minusDays(3), library, ItemStatus.LOST, alice, report1);

        // Report 2 (Bob): 1 item
        Report report2 = new Report(null, "Found Notebook", "Physics notebook found in cafeteria.",
                LocalDateTime.now().minusDays(2), ReportType.FOUND_ITEM, ReportStatus.IN_PROGRESS, bob, null);
        report2 = reportRepository.save(report2);

        Item item2 = new Item(null, "Notebook", "Blue notebook with physics notes", "Stationery", "https://example.com/notebook.jpg",
                LocalDateTime.now().minusDays(2), cafeteria, ItemStatus.FOUND, bob, report2);

        // Report 3 (Alice): 2 items
        Report report3 = new Report(null, "Lost Electronics", "Lost charger and mouse in lab.",
                LocalDateTime.now().minusDays(1), ReportType.LOST_ITEM, ReportStatus.RESOLVED, alice, null);
        report3 = reportRepository.save(report3);

        Item item3 = new Item(null, "Charger", "MacBook charger", "Electronics", "https://example.com/charger.jpg",
                LocalDateTime.now().minusDays(1), lab, ItemStatus.LOST, alice, report3);

        Item item4 = new Item(null, "Mouse", "Bluetooth mouse", "Electronics", "https://example.com/mouse.jpg",
                LocalDateTime.now().minusDays(1), lab, ItemStatus.LOST, alice, report3);

        // Report 4 (Charlie): 1 item
        Report report4 = new Report(null, "Found USB Stick", "Found near library printer.",
                LocalDateTime.now().minusDays(2), ReportType.FOUND_ITEM, ReportStatus.OPEN, charlie, null);
        report4 = reportRepository.save(report4);

        Item item5 = new Item(null, "USB Stick", "16GB USB found in printer tray", "Electronics", "https://example.com/usb.jpg",
                LocalDateTime.now().minusDays(2), library, ItemStatus.FOUND, charlie, report4);

        // Save items
        itemRepository.saveAll(List.of(item1, item2, item3, item4, item5));

        // Attach items to reports
        report1.setItems(List.of(item1));
        report2.setItems(List.of(item2));
        report3.setItems(List.of(item3, item4));
        report4.setItems(List.of(item5));

        reportRepository.saveAll(List.of(report1, report2, report3, report4));
    }
}