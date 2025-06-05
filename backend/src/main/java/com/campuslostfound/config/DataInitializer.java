package com.campuslostfound.config;

import com.campuslostfound.model.*;
import com.campuslostfound.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final ReportRepository reportRepository;
    private final ItemRepository itemRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Create users
            User alice = new User(null, "Alice", "alice@example.com", null, null, null);
            User bob = new User(null, "Bob", "bob@example.com", null, null, null);
            User charlie = new User(null, "Charlie", "charlie@example.com", null, null, null);
            userRepository.save(alice);
            userRepository.save(bob);
            userRepository.save(charlie);

            // Create locations
            Location library = new Location(null, "Library", "Main campus library", null, null);
            Location cafeteria = new Location(null, "Cafeteria", "Student cafeteria", null, null);
            Location lab = new Location(null, "Physics Lab", "Near equipment room", null, null);
            locationRepository.save(library);
            locationRepository.save(cafeteria);
            locationRepository.save(lab);

            // Create reports
            Report lostBookReport = new Report(null, alice, library, null, true, null);
            Report foundPhoneReport = new Report(null, bob, cafeteria, null, true, null);
            Report lostElectronicsReport = new Report(null, alice, lab, null, false, null);
            Report foundUSBReport = new Report(null, charlie, library, null, true, null);
            reportRepository.save(lostBookReport);
            reportRepository.save(foundPhoneReport);
            reportRepository.save(lostElectronicsReport);
            reportRepository.save(foundUSBReport);

            // Create items
            Item book = new Item(null, "Calculus Textbook", "Blue cover, Stewart 8th Edition", lostBookReport, null, null);
            Item phone = new Item(null, "iPhone 12", "Black, with cracked screen", foundPhoneReport, alice, null);
            Item charger = new Item(null, "MacBook Charger", "61W USB-C Power Adapter", lostElectronicsReport, null, null);
            Item mouse = new Item(null, "Bluetooth Mouse", "Wireless mouse, black", lostElectronicsReport, null, null);
            Item usbStick = new Item(null, "16GB USB Stick", "USB 3.0 flash drive", foundUSBReport, null, null);
            itemRepository.save(book);
            itemRepository.save(phone);
            itemRepository.save(charger);
            itemRepository.save(mouse);
            itemRepository.save(usbStick);
        };
    }
}