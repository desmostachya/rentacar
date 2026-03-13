package com.kush.config;

import com.kush.entity.*;
import com.kush.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
//@Component
public class DataInitializer {


    /**
     * CommandLineRunner Bean to populate initial data
     * Runs after application startup
     */
    @Bean
    public CommandLineRunner initializeData(
            UserRepository userRepository,
            LocationRepository locationRepository,
            VehicleRepository vehicleRepository,
            ReservationRepository reservationRepository,
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository,
            MaintenanceRepository maintenanceRepository,
            InsuranceRepository insuranceRepository,
            PasswordEncoder passwordEncoder) {
        
        return args -> {
            try {
                log.info("Starting database initialization...");

                // Check if data already exists
                if (userRepository.count() > 0) {
                    log.info("Database already populated. Skipping initialization.");
                    return;
                }

                // Initialize data
                List<User> users = initializeUsers(userRepository, passwordEncoder);
                List<Location> locations = initializeLocations(locationRepository);
                List<Vehicle> vehicles = initializeVehicles(vehicleRepository, locations);
                List<Reservation> reservations = initializeReservations(reservationRepository, users, vehicles);
                initializeInvoices(invoiceRepository,reservationRepository);
                initializePayments(paymentRepository, users);
                initializeMaintenance(maintenanceRepository, vehicles);
                initializeInsurance(insuranceRepository, vehicles);

                log.info("✅ Database initialization completed successfully!");
                log.info("Sample data has been populated.");
                log.info("Use Postman collection to test endpoints.");

            } catch (Exception e) {
                log.error("❌ Error during database initialization: {}", e.getMessage(), e);
            }
        };
    }

    /**
     * Initialize users with different roles
     */
    private List<User> initializeUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        log.info("Initializing users...");
        List<User> users = new ArrayList<>();

        // Admin User
        User admin = User.builder()
                .email("admin@rentacar.com")
                .firstName("Admin")
                .lastName("User")
                .password(passwordEncoder.encode("Admin123"))
                .phoneNumber("+1-555-0001")
                .address("123 Admin Ave")
                .city("New York")
                .postalCode("10001")
                .role(UserRole.ADMIN)
                .isActive(true)
                .build();
        users.add(admin);

        // Manager User
        User manager = User.builder()
                .email("manager@rentacar.com")
                .firstName("Manager")
                .lastName("User")
                .password(passwordEncoder.encode("Manager123"))
                .phoneNumber("+1-555-0002")
                .address("456 Manager St")
                .city("Boston")
                .postalCode("02101")
                .role(UserRole.MANAGER)
                .isActive(true)
                .build();
        users.add(manager);

        // Agent User
        User agent = User.builder()
                .email("agent@rentacar.com")
                .firstName("Agent")
                .lastName("User")
                .password(passwordEncoder.encode("Agent123"))
                .phoneNumber("+1-555-0003")
                .address("789 Agent Rd")
                .city("Los Angeles")
                .postalCode("90001")
                .role(UserRole.AGENT)
                .isActive(true)
                .build();
        users.add(agent);

        // Customer Users - 30 customers with unique data
        String[][] customerData = {
            {"john.smith@example.com", "John", "Smith", "+1-555-0100", "321 Oak St", "Chicago", "60601", "John@123"},
            {"jane.doe@example.com", "Jane", "Doe", "+1-555-0101", "654 Maple Ave", "Seattle", "98101", "Jane@123"},
            {"bob.wilson@example.com", "Bob", "Wilson", "+1-555-0102", "987 Pine Ln", "Denver", "80201", "Bob@123"},
            {"alice.johnson@example.com", "Alice", "Johnson", "+1-555-0103", "147 Elm St", "Portland", "97201", "Alice@123"},
            {"charlie.brown@example.com", "Charlie", "Brown", "+1-555-0104", "258 Oak Ave", "Austin", "78701", "Charlie@123"},
            {"diana.miller@example.com", "Diana", "Miller", "+1-555-0105", "369 Pine St", "Phoenix", "85001", "Diana@123"},
            {"emma.davis@example.com", "Emma", "Davis", "+1-555-0106", "741 Maple Ln", "San Francisco", "94102", "Emma@123"},
            {"frank.garcia@example.com", "Frank", "Garcia", "+1-555-0107", "852 Oak Dr", "Houston", "77001", "Frank@123"},
            {"grace.martinez@example.com", "Grace", "Martinez", "+1-555-0108", "963 Pine Ave", "Miami", "33101", "Grace@123"},
            {"henry.rodriguez@example.com", "Henry", "Rodriguez", "+1-555-0109", "159 Elm Dr", "Atlanta", "30301", "Henry@123"},
            {"iris.anderson@example.com", "Iris", "Anderson", "+1-555-0110", "753 Oak St", "Philadelphia", "19103", "Iris@123"},
            {"jack.taylor@example.com", "Jack", "Taylor", "+1-555-0111", "456 Maple Dr", "Washington", "20001", "Jack@123"},
            {"karen.lee@example.com", "Karen", "Lee", "+1-555-0112", "789 Pine Ln", "Boston", "02101", "Karen@123"},
            {"leo.thomas@example.com", "Leo", "Thomas", "+1-555-0113", "321 Elm St", "Las Vegas", "89101", "Leo@123"},
            {"maria.jackson@example.com", "Maria", "Jackson", "+1-555-0114", "654 Oak Ave", "Nashville", "37201", "Maria@123"},
            {"noah.white@example.com", "Noah", "White", "+1-555-0115", "987 Maple St", "Memphis", "38103", "Noah@123"},
            {"olivia.harris@example.com", "Olivia", "Harris", "+1-555-0116", "147 Pine Ave", "Detroit", "48201", "Olivia@123"},
            {"paul.martin@example.com", "Paul", "Martin", "+1-555-0117", "258 Elm Dr", "New Orleans", "70112", "Paul@123"},
            {"quinn.clark@example.com", "Quinn", "Clark", "+1-555-0118", "369 Oak Ln", "Baltimore", "21201", "Quinn@123"},
            {"ruby.lewis@example.com", "Ruby", "Lewis", "+1-555-0119", "741 Maple Ln", "Milwaukee", "53201", "Ruby@123"},
            {"sam.walker@example.com", "Sam", "Walker", "+1-555-0120", "852 Pine St", "Albuquerque", "87101", "Sam@123"},
            {"tina.hall@example.com", "Tina", "Hall", "+1-555-0121", "963 Elm Ave", "Tucson", "85701", "Tina@123"},
            {"umar.allen@example.com", "Umar", "Allen", "+1-555-0122", "159 Oak Dr", "Long Beach", "90801", "Umar@123"},
            {"vera.young@example.com", "Vera", "Young", "+1-555-0123", "753 Maple Ave", "Kansas City", "64105", "Vera@123"},
            {"william.king@example.com", "William", "King", "+1-555-0124", "456 Pine Ln", "Mesa", "85201", "William@123"},
            {"xena.wright@example.com", "Xena", "Wright", "+1-555-0125", "789 Elm St", "Virginia Beach", "23450", "Xena@123"},
            {"yara.lopez@example.com", "Yara", "Lopez", "+1-555-0126", "321 Oak Ave", "Atlanta", "30303", "Yara@123"},
            {"zack.hill@example.com", "Zack", "Hill", "+1-555-0127", "654 Maple Dr", "Austin", "78702", "Zack@123"},
            {"amber.scott@example.com", "Amber", "Scott", "+1-555-0128", "987 Pine Ave", "Seattle", "98102", "Amber@123"},
            {"brian.green@example.com", "Brian", "Green", "+1-555-0129", "147 Elm Ln", "Denver", "80202", "Brian@123"}
        };

        for (String[] data : customerData) {
            User customer = User.builder()
                    .email(data[0])
                    .firstName(data[1])
                    .lastName(data[2])
                    .password(passwordEncoder.encode(data[7]))
                    .phoneNumber(data[3])
                    .address(data[4])
                    .city(data[5])
                    .postalCode(data[6])
                    .role(UserRole.CUSTOMER)
                    .isActive(true)
                    .build();
            users.add(customer);
        }

        userRepository.saveAll(users);
        log.info("✅ Created {} users (1 Admin, 1 Manager, 1 Agent, 30 Customers)", users.size());
        return users;
    }

    /**
     * Initialize rental locations
     */
    private List<Location> initializeLocations(LocationRepository locationRepository) {
        log.info("Initializing locations...");
        List<Location> locations = new ArrayList<>();

        String[][] locationData = {
            {"Downtown Branch", "123 Main St", "New York", "10001", "+1-212-555-0100"},
            {"Airport Terminal", "456 Aviation Way", "New York", "11430", "+1-718-555-0200"},
            {"Westside Location", "789 West Park", "New York", "10025", "+1-212-555-0300"},
            {"Eastside Branch", "321 East Ave", "New York", "10065", "+1-212-555-0400"},
            {"Chicago Downtown", "500 North Michigan", "Chicago", "60611", "+1-312-555-0100"},
            {"Chicago Airport", "10000 W O'Hare", "Chicago", "60666", "+1-773-555-0200"},
            {"Boston Center", "100 Hanover St", "Boston", "02108", "+1-617-555-0100"},
            {"Seattle Downtown", "600 University St", "Seattle", "98101", "+1-206-555-0100"},
            {"Los Angeles Downtown", "333 South Hope", "Los Angeles", "90071", "+1-213-555-0100"},
            {"LA Airport", "1 World Way", "Los Angeles", "90045", "+1-310-555-0200"}
        };

        for (String[] data : locationData) {
            Location location = Location.builder()
                    .locationName(data[0])
                    .address(data[1])
                    .city(data[2])
                    .postalCode(data[3])
                    .phoneNumber(data[4])
                    .status(Location.LocationStatus.ACTIVE)
                    .build();
            locations.add(location);
        }

        locationRepository.saveAll(locations);
        log.info("✅ Created {} locations", locations.size());
        return locations;
    }

    /**
     * Initialize 50+ vehicles across different categories
     */
    private List<Vehicle> initializeVehicles(VehicleRepository vehicleRepository, List<Location> locations) {
        log.info("Initializing vehicles...");
        List<Vehicle> vehicles = new ArrayList<>();
        Random random = new Random();

        // Vehicle data: [make, model, year, category, dailyRate, fuelType, color, passengerCapacity]
        String[][] vehicleData = {
            // Economy
            {"Toyota", "Corolla", "2023", "SEDAN", "45.00", "PETROL", "Silver", "5"},
            {"Honda", "Civic", "2023", "SEDAN", "48.00", "PETROL", "Blue", "5"},
            {"Hyundai", "Elantra", "2023", "SEDAN", "42.00", "PETROL", "White", "5"},
            {"Mazda", "3", "2023", "SEDAN", "50.00", "PETROL", "Red", "5"},
            {"Kia", "Forte", "2022", "SEDAN", "43.00", "PETROL", "Black", "5"},
            {"Toyota", "Yaris", "2023", "HATCHBACK", "40.00", "PETROL", "Gray", "5"},
            {"Honda", "Fit", "2023", "HATCHBACK", "46.00", "PETROL", "White", "5"},
            {"Nissan", "Sentra", "2022", "SEDAN", "44.00", "PETROL", "Silver", "5"},
            {"Hyundai", "i20", "2023", "HATCHBACK", "39.00", "PETROL", "Blue", "5"},
            {"Kia", "Rio", "2023", "HATCHBACK", "41.00", "PETROL", "Red", "5"},
            
            // Compact
            {"Toyota", "Prius", "2023", "SEDAN", "55.00", "HYBRID", "Blue", "5"},
            {"Honda", "Accord", "2023", "SEDAN", "58.00", "PETROL", "Silver", "5"},
            {"Nissan", "Altima", "2023", "SEDAN", "56.00", "PETROL", "White", "5"},
            {"Hyundai", "Sonata", "2023", "SEDAN", "54.00", "PETROL", "Gray", "5"},
            {"Mazda", "6", "2022", "SEDAN", "60.00", "PETROL", "Black", "5"},
            {"Kia", "K5", "2023", "SEDAN", "52.00", "PETROL", "Red", "5"},
            {"Toyota", "Camry", "2023", "SEDAN", "62.00", "PETROL", "Blue", "5"},
            {"Honda", "CR-V", "2023", "SUV", "65.00", "PETROL", "Silver", "5"},
            {"Nissan", "Qashqai", "2023", "SUV", "63.00", "PETROL", "White", "5"},
            {"Hyundai", "Tucson", "2023", "SUV", "61.00", "PETROL", "Gray", "5"},
            
            // Mid-Size SUV
            {"Toyota", "RAV4", "2023", "SUV", "75.00", "PETROL", "Blue", "5"},
            {"Honda", "Odyssey", "2023", "VAN", "85.00", "PETROL", "Silver", "8"},
            {"Nissan", "Rogue", "2023", "SUV", "73.00", "PETROL", "White", "5"},
            {"Hyundai", "Santa Fe", "2023", "SUV", "78.00", "PETROL", "Gray", "7"},
            {"Mazda", "CX-5", "2023", "SUV", "80.00", "PETROL", "Red", "5"},
            {"Kia", "Sorento", "2023", "SUV", "76.00", "PETROL", "Black", "7"},
            {"Toyota", "Highlander", "2023", "SUV", "88.00", "PETROL", "Blue", "8"},
            {"Honda", "Pilot", "2023", "SUV", "90.00", "PETROL", "Silver", "8"},
            {"Nissan", "Pathfinder", "2023", "SUV", "87.00", "PETROL", "White", "7"},
            {"Hyundai", "Palisade", "2023", "SUV", "82.00", "PETROL", "Gray", "7"},
            
            // Premium SUV
            {"BMW", "X3", "2023", "SUV", "145.00", "PETROL", "Black", "5"},
            {"Mercedes", "GLC", "2023", "SUV", "155.00", "PETROL", "Silver", "5"},
            {"Audi", "Q5", "2023", "SUV", "150.00", "PETROL", "White", "5"},
            {"Lexus", "RX", "2023", "SUV", "160.00", "PETROL", "Gray", "5"},
            {"BMW", "X5", "2023", "SUV", "180.00", "PETROL", "Black", "7"},
            {"Mercedes", "GLE", "2023", "SUV", "190.00", "PETROL", "Silver", "7"},
            {"Audi", "Q7", "2023", "SUV", "185.00", "PETROL", "White", "7"},
            {"Lexus", "LX", "2023", "SUV", "210.00", "PETROL", "Gray", "8"},
            {"Porsche", "Cayenne", "2023", "SUV", "250.00", "PETROL", "Red", "5"},
            {"Tesla", "Model X", "2023", "SUV", "220.00", "ELECTRIC", "Blue", "7"},
            
            // Luxury Sedan
            {"BMW", "3 Series", "2023", "SEDAN", "120.00", "PETROL", "Black", "5"},
            {"Mercedes", "C-Class", "2023", "SEDAN", "130.00", "PETROL", "Silver", "5"},
            {"Audi", "A4", "2023", "SEDAN", "125.00", "PETROL", "White", "5"},
            {"Lexus", "ES", "2023", "SEDAN", "135.00", "PETROL", "Gray", "5"},
            {"BMW", "5 Series", "2023", "SEDAN", "150.00", "PETROL", "Black", "5"},
            {"Mercedes", "E-Class", "2023", "SEDAN", "160.00", "PETROL", "Silver", "5"},
            {"Audi", "A6", "2023", "SEDAN", "145.00", "PETROL", "White", "5"},
            {"Lexus", "LS", "2023", "SEDAN", "170.00", "PETROL", "Gray", "5"},
            {"Porsche", "911", "2023", "SEDAN", "250.00", "PETROL", "Red", "2"},
            {"Tesla", "Model S", "2023", "SEDAN", "180.00", "ELECTRIC", "Blue", "5"}
        };

        int licensePlateCounter = 1001;
        for (String[] data : vehicleData) {
            Vehicle vehicle = Vehicle.builder()
                    .licensePlate("NYC-" + licensePlateCounter++)
                    .make(data[0])
                    .model(data[1])
                    .year(Integer.parseInt(data[2]))
                    .category(Vehicle.VehicleCategory.valueOf(data[3]))
                    .color(data[6])
                    .mileage((long) (random.nextInt(50000) + 1000))
                    .dailyRate(new BigDecimal(data[4]))
                    .fuelCapacity(random.nextInt(50) + 30)
                    .fuelType(Vehicle.FuelType.valueOf(data[5]))
                    .passengerCapacity(Integer.parseInt(data[7]))
                    .hasAirConditioning(true)
                    .hasAutomaticTransmission(random.nextBoolean())
                    .description(data[0] + " " + data[1] + " (" + data[2] + ")")
                    .status(random.nextBoolean() ? Vehicle.VehicleStatus.AVAILABLE : Vehicle.VehicleStatus.RENTED)
                    .location(locations.get(random.nextInt(locations.size())))
                    .build();
            vehicles.add(vehicle);
        }

        vehicleRepository.saveAll(vehicles);
        log.info("✅ Created {} vehicles", vehicles.size());
        return vehicles;
    }

    /**
     * Initialize 155+ reservations with various statuses
     */
    private List<Reservation> initializeReservations(ReservationRepository reservationRepository,
                                                     List<User> users,
                                                     List<Vehicle> vehicles) {
        log.info("Initializing reservations...");
        List<Reservation> reservations = new ArrayList<>();
        Random random = new Random();
        LocalDate today = LocalDate.now();

        String[] statuses = {"PENDING", "CONFIRMED", "ACTIVE", "COMPLETED", "CANCELLED"};
        String[] paymentStatuses = {"PENDING", "COMPLETED", "FAILED", "REFUNDED"};
        String[] specialRequests = {
            "Child seat needed",
            "GPS needed",
            "Extra driver",
            "No smoking",
            "Pet friendly",
            "Wheelchair accessible",
            "Tow hitch needed",
            "Extra mirrors",
            "Hand controls needed",
            "Sunroof preferred",
            "Leather seats preferred",
            "Early pickup needed",
            "Late return possible",
            "Airport transportation",
            "Wedding trip",
            ""
        };

        // Get customers only (skip admin, manager, agent)
        List<User> customers = new ArrayList<>();
        for (User user : users) {
            if (user.getRole() == UserRole.CUSTOMER) {
                customers.add(user);
            }
        }

        for (int i = 1; i <= 155; i++) {
            User customer = customers.get(random.nextInt(customers.size()));
            Vehicle vehicle = vehicles.get(random.nextInt(vehicles.size()));

            int daysOffset = random.nextInt(60) - 30; // Past or future dates
            LocalDate pickupDate = today.plusDays(daysOffset);
            LocalDate returnDate = pickupDate.plusDays(random.nextInt(1, 14)); // 1-13 days rental

            String status = statuses[random.nextInt(statuses.length)];
            String paymentStatus = paymentStatuses[random.nextInt(paymentStatuses.length)];

            // Calculate total cost
            long days = java.time.temporal.ChronoUnit.DAYS.between(pickupDate, returnDate);
            BigDecimal basePrice = vehicle.getDailyRate();
            BigDecimal totalCost = basePrice.multiply(BigDecimal.valueOf(days));

            // Add discounts randomly
            if (random.nextDouble() < 0.3) {
                BigDecimal discount = totalCost.multiply(BigDecimal.valueOf(0.05 + random.nextDouble() * 0.15));
                totalCost = totalCost.subtract(discount);
            }

            // Add additional fees
            if (random.nextDouble() < 0.4) {
                totalCost = totalCost.add(new BigDecimal(25 + random.nextInt(75)));
            }

            Reservation reservation = Reservation.builder()
                    .reservationNumber("RES-" + String.format("%06d", i))
                    .pickupDate(pickupDate)
                    .returnDate(returnDate)
                    .status(Reservation.ReservationStatus.valueOf(status))
                    .totalCost(totalCost)
                    .pickupLocation("Downtown Branch")
                    .returnLocation("Downtown Branch")
                    .specialRequests(specialRequests[random.nextInt(specialRequests.length)])
                    .paymentStatus(Reservation.PaymentStatus.valueOf(paymentStatus))
                    .user(customer)
                    .vehicle(vehicle)
                    .build();

            reservations.add(reservation);
        }

        reservationRepository.saveAll(reservations);
        log.info("✅ Created {} reservations", reservations.size());
        return reservations;
    }

    /**
     * Initialize payments
     */
    private void initializePayments(PaymentRepository paymentRepository, List<User> users) {
        log.info("Initializing payments...");
        List<Payment> payments = new ArrayList<>();
        Random random = new Random();

        String[] paymentMethods = {"CREDIT_CARD", "DEBIT_CARD", "PAYPAL", "BANK_TRANSFER"};
        String[] paymentStatusArray = {"COMPLETED", "FAILED", "REFUNDED", "PENDING"};

        // Get customers only
        List<User> customers = new ArrayList<>();
        for (User user : users) {
            if (user.getRole() == UserRole.CUSTOMER) {
                customers.add(user);
            }
        }

        for (int i = 1; i <= 75; i++) {
            User customer = customers.get(random.nextInt(customers.size()));

            Payment payment = Payment.builder()
                    .paymentReference("PAY-" + String.format("%06d", i))
                    .amount(new BigDecimal(100 + random.nextInt(1900)))
                    .paymentMethod(Payment.PaymentMethod.valueOf(paymentMethods[random.nextInt(paymentMethods.length)]))
                    .paymentStatus(Payment.PaymentStatus.valueOf(paymentStatusArray[random.nextInt(paymentStatusArray.length)]))
                    .transactionId("TXN-" + String.format("%08d", 10000 + i))
                    .user(customer)
                    .build();

            payments.add(payment);
        }

        paymentRepository.saveAll(payments);
        log.info("✅ Created {} payments", payments.size());
    }

    /**
     * Initialize maintenance records
     */
    private void initializeMaintenance(MaintenanceRepository maintenanceRepository, List<Vehicle> vehicles) {
        log.info("Initializing maintenance records...");
        List<Maintenance> maintenanceList = new ArrayList<>();
        Random random = new Random();

        String[] maintenanceTypes = {
            "Oil Change",
            "Tire Rotation",
            "Filter Replacement",
            "Battery Service",
            "Brake Inspection",
            "Fluid Top-up",
            "Wheel Alignment",
            "General Inspection"
        };

        String[] maintenanceStatuses = {"SCHEDULED", "IN_PROGRESS", "COMPLETED", "CANCELLED"};

        for (Vehicle vehicle : vehicles) {
            for (int j = 0; j < random.nextInt(1, 4); j++) {
                LocalDate maintenanceDate = LocalDate.now().minusDays(random.nextInt(180));
                LocalDate completionDate = random.nextBoolean() ? maintenanceDate.plusDays(1) : null;

                Maintenance maintenance = Maintenance.builder()
                        .maintenanceType(maintenanceTypes[random.nextInt(maintenanceTypes.length)])
                        .maintenanceDate(maintenanceDate)
                        .completionDate(completionDate)
                        .description("Routine maintenance for vehicle " + vehicle.getLicensePlate())
                        .cost(new BigDecimal(50 + random.nextInt(450)))
                        .status(Maintenance.MaintenanceStatus.valueOf(maintenanceStatuses[random.nextInt(maintenanceStatuses.length)]))
                        .mileageAtService((long) (random.nextInt(50000) + 1000))
                        .nextServiceMileage((long) (random.nextInt(50000) + 10000))
                        .vehicle(vehicle)
                        .build();

                maintenanceList.add(maintenance);
            }
        }

        maintenanceRepository.saveAll(maintenanceList);
        log.info("✅ Created {} maintenance records", maintenanceList.size());
    }

    /**
     * Initialize insurance policies
     */
    private void initializeInsurance(InsuranceRepository insuranceRepository, List<Vehicle> vehicles) {
        log.info("Initializing insurance policies...");
        List<Insurance> insuranceList = new ArrayList<>();
        Random random = new Random();

        String[] insuranceProviders = {
            "SafeGuard Insurance",
            "ProTect Insurance",
            "Elite Insurance",
            "Guardian Insurance",
            "Premium Coverage Inc"
        };

        String[] insuranceTypes = {"BASIC", "COMPREHENSIVE", "PREMIUM"};

        for (Vehicle vehicle : vehicles) {
            String provider = insuranceProviders[random.nextInt(insuranceProviders.length)];
            String type = insuranceTypes[random.nextInt(insuranceTypes.length)];

            BigDecimal coverageAmount = new BigDecimal(50000 + random.nextInt(100000));
            BigDecimal premiumAmount = coverageAmount.multiply(BigDecimal.valueOf(0.01)); // 1% of coverage

            Insurance insurance = Insurance.builder()
                    .policyNumber("INS-" + String.format("%06d", vehicle.getVehicleId()))
                    .provider(provider)
                    .insuranceType(type)
                    .coverageAmount(coverageAmount)
                    .premiumAmount(premiumAmount)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusYears(1))
                    .status(Insurance.InsuranceStatus.ACTIVE)
                    .policyDetails(type + " insurance coverage for " + vehicle.getMake() + " " + vehicle.getModel())
                    .vehicle(vehicle)
                    .build();

            insuranceList.add(insurance);
        }

        insuranceRepository.saveAll(insuranceList);
        log.info("✅ Created {} insurance policies", insuranceList.size());
    }

    private void initializeInvoices(InvoiceRepository invoiceRepository,
                                    ReservationRepository reservationRepository) {
        log.info("Initializing invoices...");

        // Get reservations to link invoices to them
        Reservation reservation1 = reservationRepository.findByReservationNumber("RES-000001").orElseThrow();
        Reservation reservation2 = reservationRepository.findByReservationNumber("RES-000002").orElseThrow();
        Reservation reservation3 = reservationRepository.findByReservationNumber("RES-000003").orElseThrow();

        Invoice invoice1 = Invoice.builder()
                .invoiceNumber("INV-001")
                .reservation(reservation1)
                .invoiceDate(LocalDate.now().minusDays(30))
                .dueDate(LocalDate.now().minusDays(20))
                .subtotal(new BigDecimal("225.00"))
                .tax(new BigDecimal("25.00"))
                .totalAmount(new BigDecimal("250.00"))
                .discount(new BigDecimal("0.00"))
                .description("Sedan rental for 5 days")
                .status(Invoice.InvoiceStatus.PAID)
                .notes("Invoice for past rental - payment received")
                .build();

        Invoice invoice2 = Invoice.builder()
                .invoiceNumber("INV-002")
                .reservation(reservation2)
                .invoiceDate(LocalDate.now().minusDays(15))
                .dueDate(LocalDate.now().plusDays(15))
                .subtotal(new BigDecimal("472.50"))
                .tax(new BigDecimal("52.50"))
                .totalAmount(new BigDecimal("525.00"))
                .discount(new BigDecimal("0.00"))
                .description("SUV rental for 7 days")
                .status(Invoice.InvoiceStatus.SENT)
                .notes("Invoice for upcoming rental")
                .build();

        Invoice invoice3 = Invoice.builder()
                .invoiceNumber("INV-003")
                .reservation(reservation3)
                .invoiceDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(30))
                .subtotal(new BigDecimal("135.00"))
                .tax(new BigDecimal("15.00"))
                .totalAmount(new BigDecimal("150.00"))
                .discount(new BigDecimal("5.00"))
                .description("Economy car rental for 3 days")
                .status(Invoice.InvoiceStatus.DRAFT)
                .notes("Draft invoice - pending confirmation")
                .build();

        invoiceRepository.saveAll(Arrays.asList(invoice1, invoice2, invoice3));
        log.info("✅ Created 3 sample invoices");
    }
}