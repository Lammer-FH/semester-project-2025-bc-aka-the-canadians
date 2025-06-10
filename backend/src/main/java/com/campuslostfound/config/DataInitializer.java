package com.campuslostfound.config;

import com.campuslostfound.model.*;
import com.campuslostfound.repository.ItemRepository;
import com.campuslostfound.repository.LocationRepository;
import com.campuslostfound.repository.ReportRepository;
import com.campuslostfound.repository.UserRepository;

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
            try {
                // Create users
                User alice = new User(null, "Alice", "alice@example.com", null, null, null);
                User bob = new User(null, "Bob", "bob@example.com", null, null, null);
                User charlie = new User(null, "Charlie", "charlie@example.com", null, null, null);

                userRepository.save(alice);
                userRepository.save(bob);
                userRepository.save(charlie);
                // Flush to ensure IDs are generated
                userRepository.flush();

                // Create locations
                Location library = new Location(null, "Library", "Main campus library", null, null);
                Location cafeteria =
                    new Location(null, "Cafeteria", "Student cafeteria", null, null);
                Location lab = new Location(null, "Physics Lab", "Near equipment room", null, null);
                locationRepository.save(library);
                locationRepository.save(cafeteria);
                locationRepository.save(lab);

                // Flush to ensure IDs are generated
                locationRepository.flush();


                // Create reports
                Report lostBookReport = new Report(null, alice, library, null, ReportType.LOST, ReportStatus.OPEN, null);
                Report foundPhoneReport = new Report(null, bob, cafeteria, null, ReportType.FOUND, ReportStatus.RESOLVED, null);
                Report lostElectronicsReport = new Report(null, alice, lab, null, ReportType.LOST, ReportStatus.OPEN, null);
                Report lostUSBReport = new Report(null, charlie, library, null, ReportType.LOST, ReportStatus.RESOLVED, null);
                reportRepository.save(lostBookReport);
                reportRepository.save(foundPhoneReport);
                reportRepository.save(lostElectronicsReport);
                reportRepository.save(lostUSBReport);

                // Create items
                Item book = new Item(null, "Calculus Textbook", "Blue cover, Stewart 8th Edition", ItemStatus.UNCLAIMED, lostBookReport, null, null);
                Item phone = new Item(null, "iPhone 12", "Black, with cracked screen", ItemStatus.CLAIMED, foundPhoneReport, alice, null);
                Item mouse = new Item(null, "Bluetooth Mouse", "Wireless mouse, black", ItemStatus.CLAIMED, lostElectronicsReport, null, null);
                Item usbStick = new Item(null, "16GB USB Stick", "USB 3.0 flash drive", ItemStatus.CLAIMED, lostUSBReport, null, null);
                itemRepository.save(book);
                itemRepository.save(phone);
                itemRepository.save(mouse);
                itemRepository.save(usbStick);

            } catch (Exception e) {
                // Ignore any violations during initialization so that the application doesn't crash
            }
        };
    }
}
