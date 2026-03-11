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
import java.util.Arrays;

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
                initializeUsers(userRepository, passwordEncoder);
                initializeLocations(locationRepository);
                initializeVehicles(vehicleRepository, locationRepository);
                initializeReservations(reservationRepository, userRepository, vehicleRepository);
                initializePayments(paymentRepository, userRepository);
                initializeMaintenance(maintenanceRepository, vehicleRepository);
                initializeInsurance(insuranceRepository, vehicleRepository);

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
    private void initializeUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        log.info("Initializing users...");

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

        // Customer User 1
        User customer1 = User.builder()
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .password(passwordEncoder.encode("Password123"))
                .phoneNumber("+1-555-0100")
                .address("321 Oak St")
                .city("Chicago")
                .postalCode("60601")
                .role(UserRole.CUSTOMER)
                .isActive(true)
                .build();

        // Customer User 2
        User customer2 = User.builder()
                .email("jane@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .password(passwordEncoder.encode("Password123"))
                .phoneNumber("+1-555-0101")
                .address("654 Maple Ave")
                .city("Seattle")
                .postalCode("98101")
                .role(UserRole.CUSTOMER)
                .isActive(true)
                .build();

        // Customer User 3
        User customer3 = User.builder()
                .email("bob@example.com")
                .firstName("Bob")
                .lastName("Wilson")
                .password(passwordEncoder.encode("Password123"))
                .phoneNumber("+1-555-0102")
                .address("987 Pine Ln")
                .city("Denver")
                .postalCode("80201")
                .role(UserRole.CUSTOMER)
                .isActive(true)
                .build();

        userRepository.saveAll(Arrays.asList(admin, manager, agent, customer1, customer2, customer3));
        log.info("✅ Created 6 users (1 Admin, 1 Manager, 1 Agent, 3 Customers)");
    }

    /**
     * Initialize rental locations
     */
    private void initializeLocations(LocationRepository locationRepository) {
        log.info("Initializing locations...");

        Location downtown = Location.builder()
                .locationName("Downtown Branch")
                .address("123 Main St")
                .city("New York")
                .postalCode("10001")
                .phoneNumber("+1-212-555-0100")
                .status(Location.LocationStatus.ACTIVE)
                .build();

        Location airport = Location.builder()
                .locationName("Airport Terminal")
                .address("456 Aviation Way")
                .city("New York")
                .postalCode("11430")
                .phoneNumber("+1-718-555-0200")
                .status(Location.LocationStatus.ACTIVE)
                .build();

        Location westside = Location.builder()
                .locationName("Westside Location")
                .address("789 West Park")
                .city("New York")
                .postalCode("10025")
                .phoneNumber("+1-212-555-0300")
                .status(Location.LocationStatus.ACTIVE)
                .build();

        Location eastside = Location.builder()
                .locationName("Eastside Branch")
                .address("321 East Ave")
                .city("New York")
                .postalCode("10065")
                .phoneNumber("+1-212-555-0400")
                .status(Location.LocationStatus.ACTIVE)
                .build();

        locationRepository.saveAll(Arrays.asList(downtown, airport, westside, eastside));
        log.info("✅ Created 4 rental locations");
    }

    /**
     * Initialize vehicle fleet
     */
    private void initializeVehicles(VehicleRepository vehicleRepository, LocationRepository locationRepository) {
        log.info("Initializing vehicles...");

        Location downtown = locationRepository.findByLocationName("Downtown Branch").orElseThrow();
        Location airport = locationRepository.findByLocationName("Airport Terminal").orElseThrow();
        Location westside = locationRepository.findByLocationName("Westside Location").orElseThrow();

        // Sedan Vehicles
        Vehicle sedan1 = Vehicle.builder()
                .licensePlate("NYC-001")
                .make("Toyota")
                .model("Camry")
                .year(2024)
                .category(Vehicle.VehicleCategory.SEDAN)
                .color("Black")
                .mileage(1000L)
                .dailyRate(new BigDecimal("50.00"))
                .fuelCapacity(60)
                .fuelType(Vehicle.FuelType.PETROL)
                .passengerCapacity(5)
                .hasAirConditioning(true)
                .hasAutomaticTransmission(true)
                .description("2024 Toyota Camry in excellent condition")
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .location(downtown)
                .build();

        Vehicle sedan2 = Vehicle.builder()
                .licensePlate("NYC-002")
                .make("Honda")
                .model("Accord")
                .year(2024)
                .category(Vehicle.VehicleCategory.SEDAN)
                .color("Silver")
                .mileage(800L)
                .dailyRate(new BigDecimal("55.00"))
                .fuelCapacity(60)
                .fuelType(Vehicle.FuelType.PETROL)
                .passengerCapacity(5)
                .hasAirConditioning(true)
                .hasAutomaticTransmission(true)
                .description("2024 Honda Accord with premium features")
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .location(airport)
                .build();

        // SUV Vehicles
        Vehicle suv1 = Vehicle.builder()
                .licensePlate("NYC-101")
                .make("Toyota")
                .model("RAV4")
                .year(2024)
                .category(Vehicle.VehicleCategory.SUV)
                .color("White")
                .mileage(500L)
                .dailyRate(new BigDecimal("75.00"))
                .fuelCapacity(70)
                .fuelType(Vehicle.FuelType.PETROL)
                .passengerCapacity(7)
                .hasAirConditioning(true)
                .hasAutomaticTransmission(true)
                .description("2024 Toyota RAV4 with all safety features")
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .location(downtown)
                .build();

        Vehicle suv2 = Vehicle.builder()
                .licensePlate("NYC-102")
                .make("Ford")
                .model("Explorer")
                .year(2023)
                .category(Vehicle.VehicleCategory.SUV)
                .color("Black")
                .mileage(5000L)
                .dailyRate(new BigDecimal("80.00"))
                .fuelCapacity(80)
                .fuelType(Vehicle.FuelType.DIESEL)
                .passengerCapacity(7)
                .hasAirConditioning(true)
                .hasAutomaticTransmission(true)
                .description("2023 Ford Explorer spacious SUV")
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .location(westside)
                .build();

        // Luxury Vehicles
        Vehicle luxury1 = Vehicle.builder()
                .licensePlate("NYC-201")
                .make("BMW")
                .model("7 Series")
                .year(2024)
                .category(Vehicle.VehicleCategory.LUXURY)
                .color("Dark Blue")
                .mileage(200L)
                .dailyRate(new BigDecimal("150.00"))
                .fuelCapacity(65)
                .fuelType(Vehicle.FuelType.PETROL)
                .passengerCapacity(5)
                .hasAirConditioning(true)
                .hasAutomaticTransmission(true)
                .description("2024 BMW 7 Series luxury sedan")
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .location(downtown)
                .build();

        Vehicle luxury2 = Vehicle.builder()
                .licensePlate("NYC-202")
                .make("Mercedes")
                .model("E-Class")
                .year(2024)
                .category(Vehicle.VehicleCategory.LUXURY)
                .color("Silver")
                .mileage(300L)
                .dailyRate(new BigDecimal("145.00"))
                .fuelCapacity(65)
                .fuelType(Vehicle.FuelType.PETROL)
                .passengerCapacity(5)
                .hasAirConditioning(true)
                .hasAutomaticTransmission(true)
                .description("2024 Mercedes E-Class premium sedan")
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .location(airport)
                .build();

        // Economy Vehicles
        Vehicle economy1 = Vehicle.builder()
                .licensePlate("NYC-301")
                .make("Toyota")
                .model("Corolla")
                .year(2023)
                .category(Vehicle.VehicleCategory.ECONOMY)
                .color("Red")
                .mileage(8000L)
                .dailyRate(new BigDecimal("35.00"))
                .fuelCapacity(50)
                .fuelType(Vehicle.FuelType.PETROL)
                .passengerCapacity(5)
                .hasAirConditioning(true)
                .hasAutomaticTransmission(true)
                .description("2023 Toyota Corolla budget-friendly")
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .location(downtown)
                .build();

        // Van
        Vehicle van1 = Vehicle.builder()
                .licensePlate("NYC-401")
                .make("Chrysler")
                .model("Pacifica")
                .year(2023)
                .category(Vehicle.VehicleCategory.VAN)
                .color("Gray")
                .mileage(3000L)
                .dailyRate(new BigDecimal("85.00"))
                .fuelCapacity(75)
                .fuelType(Vehicle.FuelType.PETROL)
                .passengerCapacity(8)
                .hasAirConditioning(true)
                .hasAutomaticTransmission(true)
                .description("2023 Chrysler Pacifica family van")
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .location(westside)
                .build();

        vehicleRepository.saveAll(Arrays.asList(sedan1, sedan2, suv1, suv2, luxury1, luxury2, economy1, van1));
        log.info("✅ Created 8 vehicles (2 Sedan, 2 SUV, 2 Luxury, 1 Economy, 1 Van)");
    }

    /**
     * Initialize sample reservations
     */
    private void initializeReservations(ReservationRepository reservationRepository, 
                                       UserRepository userRepository,
                                       VehicleRepository vehicleRepository) {
        log.info("Initializing reservations...");

        User customer1 = userRepository.findByEmail("john@example.com").orElseThrow();
        Vehicle vehicle1 = vehicleRepository.findByLicensePlate("NYC-001").orElseThrow();
        Vehicle vehicle2 = vehicleRepository.findByLicensePlate("NYC-101").orElseThrow();

        Reservation reservation1 = Reservation.builder()
                .reservationNumber("RES-001")
                .pickupDate(LocalDate.now().plusDays(7))
                .returnDate(LocalDate.now().plusDays(12))
                .status(Reservation.ReservationStatus.PENDING)
                .totalCost(new BigDecimal("250.00"))
                .pickupLocation("Downtown Branch")
                .returnLocation("Downtown Branch")
                .specialRequests("Child seat needed")
                .paymentStatus(Reservation.PaymentStatus.PENDING)
                .user(customer1)
                .vehicle(vehicle1)
                .build();

        Reservation reservation2 = Reservation.builder()
                .reservationNumber("RES-002")
                .pickupDate(LocalDate.now().plusDays(14))
                .returnDate(LocalDate.now().plusDays(21))
                .status(Reservation.ReservationStatus.CONFIRMED)
                .totalCost(new BigDecimal("525.00"))
                .pickupLocation("Airport Terminal")
                .returnLocation("Downtown Branch")
                .specialRequests("GPS needed")
                .paymentStatus(Reservation.PaymentStatus.PENDING)
                .user(customer1)
                .vehicle(vehicle2)
                .build();

        reservationRepository.saveAll(Arrays.asList(reservation1, reservation2));
        log.info("✅ Created 2 sample reservations");
    }

    /**
     * Initialize sample payments
     */
    private void initializePayments(PaymentRepository paymentRepository,
                                   UserRepository userRepository) {
        log.info("Initializing payments...");

        User customer1 = userRepository.findByEmail("john@example.com").orElseThrow();
        User customer2 = userRepository.findByEmail("jane@example.com").orElseThrow();

        Payment payment1 = Payment.builder()
                .paymentReference("PAY-001")
                .amount(new BigDecimal("250.00"))
                .paymentMethod(Payment.PaymentMethod.CREDIT_CARD)
                .paymentStatus(Payment.PaymentStatus.COMPLETED)
                .transactionId("TXN-12345")
                .user(customer1)
                .build();

        Payment payment2 = Payment.builder()
                .paymentReference("PAY-002")
                .amount(new BigDecimal("150.00"))
                .paymentMethod(Payment.PaymentMethod.DEBIT_CARD)
                .paymentStatus(Payment.PaymentStatus.COMPLETED)
                .transactionId("TXN-12346")
                .user(customer2)
                .build();

        paymentRepository.saveAll(Arrays.asList(payment1, payment2));
        log.info("✅ Created 2 sample payments");
    }

    /**
     * Initialize maintenance records
     */
    private void initializeMaintenance(MaintenanceRepository maintenanceRepository,
                                      VehicleRepository vehicleRepository) {
        log.info("Initializing maintenance records...");

        Vehicle vehicle1 = vehicleRepository.findByLicensePlate("NYC-001").orElseThrow();
        Vehicle vehicle2 = vehicleRepository.findByLicensePlate("NYC-301").orElseThrow();

        Maintenance maintenance1 = Maintenance.builder()
                .maintenanceType("Oil Change")
                .maintenanceDate(LocalDate.now().minusDays(10))
                .completionDate(LocalDate.now().minusDays(9))
                .description("Regular oil and filter change")
                .cost(new BigDecimal("75.00"))
                .status(Maintenance.MaintenanceStatus.COMPLETED)
                .mileageAtService(1000L)
                .nextServiceMileage(10000L)
                .vehicle(vehicle1)
                .build();

        Maintenance maintenance2 = Maintenance.builder()
                .maintenanceType("Tire Rotation")
                .maintenanceDate(LocalDate.now().plusDays(5))
                .description("Rotate all tires for even wear")
                .cost(new BigDecimal("60.00"))
                .status(Maintenance.MaintenanceStatus.SCHEDULED)
                .mileageAtService(8000L)
                .vehicle(vehicle2)
                .build();

        maintenanceRepository.saveAll(Arrays.asList(maintenance1, maintenance2));
        log.info("✅ Created 2 maintenance records");
    }

    /**
     * Initialize insurance policies
     */
    private void initializeInsurance(InsuranceRepository insuranceRepository,
                                    VehicleRepository vehicleRepository) {
        log.info("Initializing insurance policies...");

        Vehicle vehicle1 = vehicleRepository.findByLicensePlate("NYC-001").orElseThrow();
        Vehicle vehicle2 = vehicleRepository.findByLicensePlate("NYC-101").orElseThrow();
        Vehicle vehicle3 = vehicleRepository.findByLicensePlate("NYC-201").orElseThrow();

        Insurance insurance1 = Insurance.builder()
                .policyNumber("INS-001")
                .provider("SafeGuard Insurance")
                .insuranceType("Comprehensive")
                .coverageAmount(new BigDecimal("50000.00"))
                .premiumAmount(new BigDecimal("500.00"))
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .status(Insurance.InsuranceStatus.ACTIVE)
                .policyDetails("Full coverage including theft and damage")
                .vehicle(vehicle1)
                .build();

        Insurance insurance2 = Insurance.builder()
                .policyNumber("INS-002")
                .provider("ProTect Insurance")
                .insuranceType("Comprehensive")
                .coverageAmount(new BigDecimal("75000.00"))
                .premiumAmount(new BigDecimal("750.00"))
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .status(Insurance.InsuranceStatus.ACTIVE)
                .policyDetails("Premium coverage with roadside assistance")
                .vehicle(vehicle2)
                .build();

        Insurance insurance3 = Insurance.builder()
                .policyNumber("INS-003")
                .provider("Elite Insurance")
                .insuranceType("Premium")
                .coverageAmount(new BigDecimal("100000.00"))
                .premiumAmount(new BigDecimal("1000.00"))
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .status(Insurance.InsuranceStatus.ACTIVE)
                .policyDetails("Luxury car premium coverage")
                .vehicle(vehicle3)
                .build();

        insuranceRepository.saveAll(Arrays.asList(insurance1, insurance2, insurance3));
        log.info("✅ Created 3 insurance policies");
    }
}
