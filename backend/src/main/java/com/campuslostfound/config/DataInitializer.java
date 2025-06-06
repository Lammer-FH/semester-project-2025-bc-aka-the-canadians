package com.campuslostfound.config;

import com.campuslostfound.model.Item;
import com.campuslostfound.model.ItemStatus;
import com.campuslostfound.model.Location;
import com.campuslostfound.model.Report;
import com.campuslostfound.model.ReportStatus;
import com.campuslostfound.model.User;
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
                // Check if data already exists to avoid duplicates
                if (userRepository.count() > 0) {
                    System.out.println("Data already exists, skipping initialization.");
                    return;
                }

                // Create users
                User alice = new User();
                alice.setName("Alice");
                alice.setEmail("alice@example.com");

                User bob = new User();
                bob.setName("Bob");
                bob.setEmail("bob@example.com");

                User charlie = new User();
                charlie.setName("Charlie");
                charlie.setEmail("charlie@example.com");

                userRepository.save(alice);
                userRepository.save(bob);
                userRepository.save(charlie);

                // Create locations with building info
                Location library = new Location();
                library.setName("Library");
                library.setDescription("Main campus library");
                library.setBuilding("Main Building");
                library.setFloor("2");
                library.setRoomNumber("201");

                Location cafeteria = new Location();
                cafeteria.setName("Cafeteria");
                cafeteria.setDescription("Student cafeteria");
                cafeteria.setBuilding("Student Center");
                cafeteria.setFloor("1");

                Location lab = new Location();
                lab.setName("Physics Lab");
                lab.setDescription("Near equipment room");
                lab.setBuilding("Science Building");
                lab.setFloor("3");
                lab.setRoomNumber("301");

                locationRepository.save(library);
                locationRepository.save(cafeteria);
                locationRepository.save(lab);

                // Create reports
                Report lostBookReport = new Report();
                lostBookReport.setTitle("Lost Calculus Book");
                lostBookReport.setDescription("Blue cover, Stewart 8th Edition");
                lostBookReport.setUser(alice);
                lostBookReport.setLocation(library);
                lostBookReport.setStatus(ReportStatus.OPEN);

                Report foundPhoneReport = new Report();
                foundPhoneReport.setTitle("Found iPhone");
                foundPhoneReport.setDescription("Black iPhone with cracked screen");
                foundPhoneReport.setUser(bob);
                foundPhoneReport.setLocation(cafeteria);
                foundPhoneReport.setStatus(ReportStatus.OPEN);

                Report lostElectronicsReport = new Report();
                lostElectronicsReport.setTitle("Lost Electronics");
                lostElectronicsReport.setDescription("MacBook charger and wireless mouse");
                lostElectronicsReport.setUser(alice);
                lostElectronicsReport.setLocation(lab);
                lostElectronicsReport.setStatus(ReportStatus.OPEN);

                Report foundUSBReport = new Report();
                foundUSBReport.setTitle("Found USB Stick");
                foundUSBReport.setDescription("16GB USB 3.0 flash drive");
                foundUSBReport.setUser(charlie);
                foundUSBReport.setLocation(library);
                foundUSBReport.setStatus(ReportStatus.OPEN);

                reportRepository.save(lostBookReport);
                reportRepository.save(foundPhoneReport);
                reportRepository.save(lostElectronicsReport);
                reportRepository.save(foundUSBReport);

                // Create items
                Item book = new Item();
                book.setName("Calculus Textbook");
                book.setDescription("Blue cover, Stewart 8th Edition");
                book.setCategory("Books");
                book.setReport(lostBookReport);
                book.setStatus(ItemStatus.LOST);

                Item phone = new Item();
                phone.setName("iPhone 12");
                phone.setDescription("Black, with cracked screen");
                phone.setCategory("Electronics");
                phone.setReport(foundPhoneReport);
                phone.setClaimedByUser(alice);
                phone.setStatus(ItemStatus.FOUND);

                Item charger = new Item();
                charger.setName("MacBook Charger");
                charger.setDescription("61W USB-C Power Adapter");
                charger.setCategory("Electronics");
                charger.setReport(lostElectronicsReport);
                charger.setStatus(ItemStatus.LOST);

                Item mouse = new Item();
                mouse.setName("Bluetooth Mouse");
                mouse.setDescription("Wireless mouse, black");
                mouse.setCategory("Electronics");
                mouse.setReport(lostElectronicsReport);
                mouse.setStatus(ItemStatus.LOST);

                Item usbStick = new Item();
                usbStick.setName("16GB USB Stick");
                usbStick.setDescription("USB 3.0 flash drive");
                usbStick.setCategory("Electronics");
                usbStick.setReport(foundUSBReport);
                usbStick.setStatus(ItemStatus.FOUND);

                itemRepository.save(book);
                itemRepository.save(phone);
                itemRepository.save(charger);
                itemRepository.save(mouse);
                itemRepository.save(usbStick);

                System.out.println("Test data initialized successfully!");

            } catch (Exception e) {
                // Ignore any violations during initialization so that the application doesn't crash
                System.out.println("Error during data initialization: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}
