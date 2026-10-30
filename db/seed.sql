USE gudos_nest;

-- Sample bcrypt-format hash (60 chars) reused for every user.
-- You may regenerate a new hash using HashGen.java
SET @pw = '$2a$10$cTTDtwkWJAuvdboju6htYu/RUzB3LBS8VyKwpKeWvB/VrneWprkNK';

-- ------------------------------------------------------------
-- 1. Users (30): ids 1-6 owners, 7-8 owner + renter, 9-30 renters
-- ------------------------------------------------------------
INSERT INTO Users (full_name, email, password_hash, is_owner, is_renter, account_status) VALUES
('Maria Clara Santos',     'maria.santos@example.com',      @pw, TRUE,  FALSE, 'active'),
('Roberto Dela Cruz',      'roberto.delacruz@example.com',  @pw, TRUE,  FALSE, 'active'),
('Teresita Magsaysay',     'teresita.magsaysay@example.com',@pw, TRUE,  FALSE, 'active'),
('Eduardo Villanueva',     'eduardo.villanueva@example.com',@pw, TRUE,  FALSE, 'active'),
('Corazon Bautista',       'corazon.bautista@example.com',  @pw, TRUE,  FALSE, 'active'),
('Antonio Reyes',          'antonio.reyes@example.com',     @pw, TRUE,  FALSE, 'active'),
('Lourdes Fernandez',      'lourdes.fernandez@example.com', @pw, TRUE,  TRUE,  'active'),
('Ramon Aquino',           'ramon.aquino@example.com',      @pw, TRUE,  TRUE,  'active'),
('Angelica Mendoza',       'angelica.mendoza@example.com',  @pw, FALSE, TRUE,  'active'),
('Jonathan Cabrera',       'jonathan.cabrera@example.com',  @pw, FALSE, TRUE,  'active'),
('Bianca Lim',             'bianca.lim@example.com',        @pw, FALSE, TRUE,  'active'),
('Miguel Torres',          'miguel.torres@example.com',     @pw, FALSE, TRUE,  'active'),
('Katrina Salazar',        'katrina.salazar@example.com',   @pw, FALSE, TRUE,  'active'),
('Daniel Ocampo',          'daniel.ocampo@example.com',     @pw, FALSE, TRUE,  'active'),
('Sophia Pineda',          'sophia.pineda@example.com',     @pw, FALSE, TRUE,  'active'),
('Gabriel Navarro',        'gabriel.navarro@example.com',   @pw, FALSE, TRUE,  'active'),
('Isabel Domingo',         'isabel.domingo@example.com',    @pw, FALSE, TRUE,  'active'),
('Christian Yap',          'christian.yap@example.com',     @pw, FALSE, TRUE,  'suspended'),
('Patricia Ramos',         'patricia.ramos@example.com',    @pw, FALSE, TRUE,  'active'),
('Joshua Tan',             'joshua.tan@example.com',        @pw, FALSE, TRUE,  'active'),
('Camille Valdez',         'camille.valdez@example.com',    @pw, FALSE, TRUE,  'active'),
('Nathan Cruz',            'nathan.cruz@example.com',       @pw, FALSE, TRUE,  'active'),
('Andrea Soriano',         'andrea.soriano@example.com',    @pw, FALSE, TRUE,  'active'),
('Kevin Lopez',            'kevin.lopez@example.com',       @pw, FALSE, TRUE,  'active'),
('Michelle Go',            'michelle.go@example.com',       @pw, FALSE, TRUE,  'active'),
('Francis Abad',           'francis.abad@example.com',      @pw, FALSE, TRUE,  'active'),
('Jasmine Uy',             'jasmine.uy@example.com',        @pw, FALSE, TRUE,  'active'),
('Carlo Manalo',           'carlo.manalo@example.com',      @pw, FALSE, TRUE,  'active'),
('Rhea Estrada',           'rhea.estrada@example.com',      @pw, FALSE, TRUE,  'active'),
('Vincent Chua',           'vincent.chua@example.com',      @pw, FALSE, TRUE,  'suspended');

-- ------------------------------------------------------------
-- 2. Properties (12)
-- ------------------------------------------------------------
INSERT INTO Properties (owner_id, property_name, location, acquisition_cost, acquisition_date) VALUES
(1, 'Casa Bella Residences',    'Lanang, Davao City, Davao del Sur',                  18500000.00, '2019-03-15'),
(2, 'Matina Heights Apartments','Matina, Davao City, Davao del Sur',                  12400000.00, '2018-07-02'),
(3, 'Samal Beach House',        'Island Garden City of Samal, Davao del Norte',        9800000.00, '2020-01-20'),
(4, 'Eden Mountain Lodge',      'Eden, Toril, Davao City, Davao del Sur',              7600000.00, '2021-05-10'),
(1, 'Poblacion Studio Suites',  'Poblacion District, Davao City, Davao del Sur',       6900000.00, '2022-02-14'),
(5, 'Buhangin Family Home',     'Buhangin, Davao City, Davao del Sur',                 5400000.00, '2017-11-30'),
(6, 'Lanang Bayview Condo',     'Lanang, Davao City, Davao del Sur',                  14200000.00, '2020-09-08'),
(7, 'Ecoland Garden Villas',    'Ecoland, Davao City, Davao del Sur',                 16800000.00, '2019-12-01'),
(2, 'Talomo Dormitel',          'Talomo, Davao City, Davao del Sur',                   4300000.00, '2016-06-18'),
(8, 'Mintal Farm Stay',         'Mintal, Davao City, Davao del Sur',                   3900000.00, '2023-03-25'),
(7, 'Bajada Loft Residences',   'Bajada, Davao City, Davao del Sur',                   8700000.00, NULL),
(3, 'Matina Pangi Townhouse',   'Matina Pangi, Davao City, Davao del Sur',             6200000.00, '2021-10-12');

-- ------------------------------------------------------------
-- 3. Units (33). Houses use an "Entire property" unit.
--    ids: P1 = 1-4, P2 = 5-8, P3 = 9, P4 = 10-12, P5 = 13-16, P6 = 17,
--         P7 = 18-21, P8 = 22-24, P9 = 25-28, P10 = 29, P11 = 30-32, P12 = 33
-- ------------------------------------------------------------
INSERT INTO Units (property_id, unit_name, unit_description, capacity, daily_rate, unit_status) VALUES
(1,  'Unit 101',        '1-bedroom condo with balcony and city view',                     3,  2500.00, 'unoccupied'),
(1,  'Unit 102',        '1-bedroom condo, fully furnished',                               3,  2500.00, 'unoccupied'),
(1,  'Unit 201',        '2-bedroom condo with full kitchen',                              5,  3000.00, 'unoccupied'),
(1,  'Unit 202',        '2-bedroom corner unit with bay view',                            5,  3500.00, 'unoccupied'),
(2,  'Apartment A1',    'Studio-type apartment near Matina Town Square',                  2,  1800.00, 'unoccupied'),
(2,  'Apartment A2',    'Studio-type apartment near Matina Town Square',                  2,  1800.00, 'unoccupied'),
(2,  'Apartment B1',    '1-bedroom apartment with parking',                               3,  2000.00, 'unoccupied'),
(2,  'Apartment B2',    '1-bedroom apartment with parking',                               3,  2200.00, 'maintenance'),
(3,  'Entire property', 'Whole beachfront house with 3 bedrooms and private beach access',10,  8500.00, 'unoccupied'),
(4,  'Cabin 1',         'Cozy cabin with fireplace and forest view',                      4,  3200.00, 'unoccupied'),
(4,  'Cabin 2',         'Cozy cabin with fireplace and forest view',                      4,  3200.00, 'unoccupied'),
(4,  'Family Cabin',    'Large cabin with two bedrooms and a kitchen',                    8,  4000.00, 'occupied'),
(5,  'Studio 1',        'Compact studio with kitchenette',                                2,  1500.00, 'unoccupied'),
(5,  'Studio 2',        'Compact studio with kitchenette',                                2,  1500.00, 'unoccupied'),
(5,  'Studio 3',        'Studio with work desk and kitchenette',                          3,  1700.00, 'occupied'),
(5,  'Studio 4',        'Studio with work desk and kitchenette',                          3,  1700.00, 'maintenance'),
(6,  'Entire property', 'Whole 4-bedroom family house with garage',                      12,  6000.00, 'unoccupied'),
(7,  'Unit 5A',         'Studio condo with sea view',                                     2,  2800.00, 'unoccupied'),
(7,  'Unit 5B',         'Studio condo with sea view',                                     2,  2800.00, 'unoccupied'),
(7,  'Unit 6A',         '1-bedroom condo with sofa bed',                                  4,  3300.00, 'occupied'),
(7,  'Unit 6B',         '1-bedroom condo with sofa bed',                                  4,  3300.00, 'unoccupied'),
(8,  'Villa 1',         'Garden villa with private plunge pool',                          6,  4500.00, 'unoccupied'),
(8,  'Villa 2',         'Garden villa with private plunge pool',                          6,  4500.00, 'unoccupied'),
(8,  'Villa 3',         'Large villa with pool and terrace',                              8,  5200.00, 'unoccupied'),
(9,  'Room 1',          'Single room with study desk, shared bathroom',                   1,   900.00, 'unoccupied'),
(9,  'Room 2',          'Single room with study desk, shared bathroom',                   1,   900.00, 'unoccupied'),
(9,  'Room 3',          'Double room with private bathroom',                              2,  1100.00, 'unoccupied'),
(9,  'Room 4',          'Double room with private bathroom',                              2,  1100.00, 'maintenance'),
(10, 'Entire property', 'Whole farm stay with bamboo cottage, garden, and fish pond',    12,  7500.00, 'unoccupied'),
(11, 'Loft 1',          'Modern loft with mezzanine bedroom',                             3,  2300.00, 'unoccupied'),
(11, 'Loft 2',          'Modern loft with mezzanine bedroom',                             3,  2300.00, 'unoccupied'),
(11, 'Loft 3',          'Loft with big windows and workspace',                            4,  2600.00, 'unoccupied'),
(12, 'Entire property', 'Whole 3-bedroom townhouse with parking',                         8,  5500.00, 'unoccupied');

-- ------------------------------------------------------------
-- 4. Bookings (33). total_amount = daily_rate x nights.
--    Check-in 14:00, check-out 12:00. 
-- ------------------------------------------------------------
INSERT INTO Bookings (renter_id, unit_id, created_at, check_in, check_out, number_of_renters, special_requests, booking_status, total_amount, cancelled_at) VALUES
( 9,  1, '2026-07-28 10:15:00', '2026-08-05 14:00:00', '2026-08-08 12:00:00',  2, 'Early check-in if possible',        'completed', 7500.00, NULL),
(10,  2, '2026-08-02 14:00:00', '2026-08-10 14:00:00', '2026-08-12 12:00:00',  3, NULL,                                'completed', 5000.00, NULL),
(11,  9, '2026-08-01 13:50:00', '2026-08-15 14:00:00', '2026-08-17 12:00:00',  6, 'Birthday celebration',              'completed',17000.00, NULL),
(12, 10, '2026-08-10 09:00:00', '2026-08-20 14:00:00', '2026-08-23 12:00:00',  4, NULL,                                'completed', 9600.00, NULL),
(13,  5, '2026-08-20 11:00:00', '2026-09-01 14:00:00', '2026-09-04 12:00:00',  2, NULL,                                'completed', 5400.00, NULL),
(14, 13, '2026-08-25 16:00:00', '2026-09-03 14:00:00', '2026-09-05 12:00:00',  1, 'Quiet floor please',                'completed', 3000.00, NULL),
(15, 17, '2026-08-28 10:00:00', '2026-09-06 14:00:00', '2026-09-09 12:00:00',  8, 'Family reunion',                    'completed',18000.00, NULL),
(16, 18, '2026-09-01 12:00:00', '2026-09-10 14:00:00', '2026-09-14 12:00:00',  2, NULL,                                'completed',11200.00, NULL),
(17, 22, '2026-09-05 09:30:00', '2026-09-12 14:00:00', '2026-09-14 12:00:00',  4, NULL,                                'completed', 9000.00, NULL),
(18, 25, '2026-09-08 15:00:00', '2026-09-15 14:00:00', '2026-09-22 12:00:00',  1, 'Student on internship',             'completed', 6300.00, NULL),
(19, 29, '2026-09-10 08:45:00', '2026-09-18 14:00:00', '2026-09-20 12:00:00', 10, 'Company team building',             'completed',15000.00, NULL),
(20, 30, '2026-09-12 13:00:00', '2026-09-20 14:00:00', '2026-09-23 12:00:00',  2, NULL,                                'completed', 6900.00, NULL),
(21, 33, '2026-09-15 17:00:00', '2026-09-25 14:00:00', '2026-09-28 12:00:00',  6, NULL,                                'completed',16500.00, NULL),
(22,  3, '2026-09-22 10:20:00', '2026-10-01 14:00:00', '2026-10-04 12:00:00',  4, NULL,                                'completed', 9000.00, NULL),
(23, 11, '2026-09-28 18:00:00', '2026-10-05 14:00:00', '2026-10-08 12:00:00',  3, NULL,                                'cancelled', 9600.00, '2026-10-01 09:30:00'),
(24,  6, '2026-10-01 08:00:00', '2026-10-08 14:00:00', '2026-10-10 12:00:00',  2, NULL,                                'cancelled', 3600.00, '2026-10-05 15:20:00'),
(25, 14, '2026-10-02 12:00:00', '2026-10-10 14:00:00', '2026-10-13 12:00:00',  2, NULL,                                'completed', 4500.00, NULL),
(26, 19, '2026-10-04 09:00:00', '2026-10-12 14:00:00', '2026-10-15 12:00:00',  2, NULL,                                'completed', 8400.00, NULL),
(27, 23, '2026-10-06 14:00:00', '2026-10-14 14:00:00', '2026-10-16 12:00:00',  5, NULL,                                'cancelled', 9000.00, '2026-10-10 11:10:00'),
(28, 26, '2026-10-08 11:30:00', '2026-10-15 14:00:00', '2026-10-20 12:00:00',  1, NULL,                                'completed', 4500.00, NULL),
(29, 31, '2026-10-10 16:00:00', '2026-10-18 14:00:00', '2026-10-21 12:00:00',  3, NULL,                                'completed', 6900.00, NULL),
(30,  4, '2026-10-12 10:00:00', '2026-10-20 14:00:00', '2026-10-24 12:00:00',  5, 'Extra pillows',                     'completed',14000.00, NULL),
( 9, 12, '2026-10-15 09:00:00', '2026-10-23 14:00:00', '2026-10-27 12:00:00',  6, NULL,                                'approved', 16000.00, NULL),
(10, 15, '2026-10-16 13:00:00', '2026-10-24 14:00:00', '2026-10-28 12:00:00',  2, NULL,                                'approved',  6800.00, NULL),
(11, 20, '2026-10-18 15:00:00', '2026-10-25 14:00:00', '2026-10-28 12:00:00',  3, 'Need parking slot',                 'approved',  9900.00, NULL),
(12,  7, '2026-10-20 12:00:00', '2026-10-29 14:00:00', '2026-11-01 12:00:00',  2, 'Late arrival around 9 PM',          'approved',  6000.00, NULL),
(13, 24, '2026-10-23 10:00:00', '2026-11-05 14:00:00', '2026-11-08 12:00:00',  7, NULL,                                'pending',  15600.00, NULL),
(14,  9, '2026-10-21 08:30:00', '2026-11-14 14:00:00', '2026-11-16 12:00:00',  8, NULL,                                'approved', 17000.00, NULL),
(15, 27, '2026-10-22 17:00:00', '2026-11-01 14:00:00', '2026-11-04 12:00:00',  2, NULL,                                'rejected',  3300.00, NULL),
(16, 32, '2026-10-24 11:00:00', '2026-11-10 14:00:00', '2026-11-13 12:00:00',  3, NULL,                                'pending',   7800.00, NULL),
(17, 17, '2026-10-19 14:00:00', '2026-12-24 14:00:00', '2026-12-27 12:00:00', 10, 'Baby crib needed',                  'approved', 18000.00, NULL),
( 7, 33, '2026-10-23 09:30:00', '2026-12-01 14:00:00', '2026-12-03 12:00:00',  4, NULL,                                'approved', 11000.00, NULL),
( 8, 21, '2026-10-22 08:50:00', '2026-11-20 14:00:00', '2026-11-22 12:00:00',  4, NULL,                                'pending',   6600.00, NULL);

-- ------------------------------------------------------------
-- 5. Payments (37): ids 1-32 rental (some pending), 33-37 damage fees
--    Paid rows must have payment_date and receipt_url.
-- ------------------------------------------------------------
INSERT INTO Payments (booking_id, amount, receipt_url, payment_type, payment_status, payment_date) VALUES
( 1,  7500.00, 'https://gudosnest.example/receipts/pay-001.jpg', 'rental_payment', 'paid',    '2026-07-28 10:40:00'),
( 2,  5000.00, 'https://gudosnest.example/receipts/pay-002.jpg', 'rental_payment', 'paid',    '2026-08-02 14:20:00'),
( 3, 17000.00, 'https://gudosnest.example/receipts/pay-003.jpg', 'rental_payment', 'paid',    '2026-08-01 15:30:00'),
( 4,  9600.00, 'https://gudosnest.example/receipts/pay-004.jpg', 'rental_payment', 'paid',    '2026-08-10 09:30:00'),
( 5,  5400.00, 'https://gudosnest.example/receipts/pay-005.jpg', 'rental_payment', 'paid',    '2026-08-20 11:30:00'),
( 6,  3000.00, 'https://gudosnest.example/receipts/pay-006.jpg', 'rental_payment', 'paid',    '2026-08-25 16:20:00'),
( 7, 18000.00, 'https://gudosnest.example/receipts/pay-007.jpg', 'rental_payment', 'paid',    '2026-08-28 10:45:00'),
( 8, 11200.00, 'https://gudosnest.example/receipts/pay-008.jpg', 'rental_payment', 'paid',    '2026-09-01 12:30:00'),
( 9,  9000.00, 'https://gudosnest.example/receipts/pay-009.jpg', 'rental_payment', 'paid',    '2026-09-05 10:00:00'),
(10,  6300.00, 'https://gudosnest.example/receipts/pay-010.jpg', 'rental_payment', 'paid',    '2026-09-08 15:20:00'),
(11, 15000.00, 'https://gudosnest.example/receipts/pay-011.jpg', 'rental_payment', 'paid',    '2026-09-10 09:15:00'),
(12,  6900.00, 'https://gudosnest.example/receipts/pay-012.jpg', 'rental_payment', 'paid',    '2026-09-12 13:30:00'),
(13, 16500.00, 'https://gudosnest.example/receipts/pay-013.jpg', 'rental_payment', 'paid',    '2026-09-15 17:30:00'),
(14,  9000.00, 'https://gudosnest.example/receipts/pay-014.jpg', 'rental_payment', 'paid',    '2026-09-22 10:50:00'),
(15,  9600.00, 'https://gudosnest.example/receipts/pay-015.jpg', 'rental_payment', 'paid',    '2026-09-28 18:20:00'),
(16,  3600.00, 'https://gudosnest.example/receipts/pay-016.jpg', 'rental_payment', 'paid',    '2026-10-01 08:30:00'),
(17,  4500.00, 'https://gudosnest.example/receipts/pay-017.jpg', 'rental_payment', 'paid',    '2026-10-02 12:20:00'),
(18,  8400.00, 'https://gudosnest.example/receipts/pay-018.jpg', 'rental_payment', 'paid',    '2026-10-04 09:30:00'),
(19,  9000.00, 'https://gudosnest.example/receipts/pay-019.jpg', 'rental_payment', 'paid',    '2026-10-06 14:30:00'),
(20,  4500.00, 'https://gudosnest.example/receipts/pay-020.jpg', 'rental_payment', 'paid',    '2026-10-08 12:00:00'),
(21,  6900.00, 'https://gudosnest.example/receipts/pay-021.jpg', 'rental_payment', 'paid',    '2026-10-10 16:30:00'),
(22, 14000.00, 'https://gudosnest.example/receipts/pay-022.jpg', 'rental_payment', 'paid',    '2026-10-12 10:30:00'),
(23, 16000.00, 'https://gudosnest.example/receipts/pay-023.jpg', 'rental_payment', 'paid',    '2026-10-15 09:40:00'),
(24,  6800.00, 'https://gudosnest.example/receipts/pay-024.jpg', 'rental_payment', 'paid',    '2026-10-16 13:30:00'),
(25,  9900.00, 'https://gudosnest.example/receipts/pay-025.jpg', 'rental_payment', 'paid',    '2026-10-18 15:20:00'),
(26,  6000.00, 'https://gudosnest.example/receipts/pay-026.jpg', 'rental_payment', 'paid',    '2026-10-20 12:30:00'),
(27, 15600.00, NULL,                                             'rental_payment', 'pending', NULL),
(28, 17000.00, 'https://gudosnest.example/receipts/pay-028.jpg', 'rental_payment', 'paid',    '2026-10-21 09:00:00'),
(30,  7800.00, NULL,                                             'rental_payment', 'pending', NULL),
(31, 18000.00, NULL,                                             'rental_payment', 'pending', NULL),
(32, 11000.00, 'https://gudosnest.example/receipts/pay-031.jpg', 'rental_payment', 'paid',    '2026-10-23 10:00:00'),
(33,  6600.00, NULL,                                             'rental_payment', 'pending', NULL),
( 4,  1500.00, 'https://gudosnest.example/receipts/pay-033.jpg', 'damage_fee',     'paid',    '2026-08-23 13:15:00'),
( 8,  2500.00, 'https://gudosnest.example/receipts/pay-034.jpg', 'damage_fee',     'paid',    '2026-09-14 14:30:00'),
(18,   800.00, 'https://gudosnest.example/receipts/pay-035.jpg', 'damage_fee',     'paid',    '2026-10-15 13:00:00'),
(10,   600.00, 'https://gudosnest.example/receipts/pay-036.jpg', 'damage_fee',     'paid',    '2026-09-22 14:00:00'),
(22,  1200.00, NULL,                                             'damage_fee',     'pending', NULL);

-- ------------------------------------------------------------
-- 6. Refunds (5)
-- ------------------------------------------------------------
INSERT INTO Refunds (payment_id, cancellation_fee, cancellation_reason, refund_status, requested_at, processed_at, refund_receipt_url) VALUES
(15,  960.00, 'Change of travel plans',                                'processed', '2026-10-01 09:35:00', '2026-10-03 10:00:00', 'https://gudosnest.example/receipts/refund-001.jpg'),
(16,  360.00, 'Family emergency',                                      'processed', '2026-10-05 15:30:00', '2026-10-07 11:15:00', 'https://gudosnest.example/receipts/refund-002.jpg'),
(19,  900.00, 'Flight was cancelled',                                  'approved',  '2026-10-10 11:10:00', NULL,                  NULL),
(33,    0.00, 'Damage was already present before check-in',            'requested', '2026-08-25 10:00:00', NULL,                  NULL),
(10,    0.00, 'Noise from nearby construction during the stay',        'rejected',  '2026-09-25 09:00:00', NULL,                  NULL);

-- ------------------------------------------------------------
-- 7. Messages (32)
-- ------------------------------------------------------------
INSERT INTO Messages (sender_id, receiver_id, booking_id, message_text, sent_at, is_read) VALUES
( 9,  1,  1, 'Hi! Is early check-in at 12 noon possible for Unit 101?',                                                                '2026-07-29 09:10:00', TRUE),
( 1,  9,  1, 'Hello Angelica! Yes, early check-in is fine as long as the unit is already cleaned.',                                    '2026-07-29 09:42:00', TRUE),
(11,  3,  3, 'Good day! Does the beach house include kayaks and life vests?',                                                          '2026-08-01 14:05:00', TRUE),
( 3, 11,  3, 'Yes, 2 kayaks and 8 life vests are included. Please return them after use.',                                             '2026-08-01 15:20:00', TRUE),
(12,  4,  4, 'Hi, we arrived and the cabin is lovely. The fireplace screen seems loose though.',                                       '2026-08-20 17:30:00', TRUE),
( 4, 12,  4, 'Thanks for letting us know, we will fix it. Please send photos if anything is damaged.',                                 '2026-08-20 18:00:00', TRUE),
( 4, 12,  4, 'After check-out we found a broken window panel. Photo: https://gudosnest.example/uploads/damage-b4-01.jpg. A damage fee of 1,500 applies.', '2026-08-23 12:45:00', TRUE),
(12,  4,  4, 'Understood, I will pay the damage fee today.',                                                                           '2026-08-23 13:10:00', TRUE),
( 6, 16,  8, 'Hi Gabriel, the sofa bed cover was stained after your stay. Photo: https://gudosnest.example/uploads/damage-b8-01.jpg. A damage fee of 2,500 applies.', '2026-09-14 13:30:00', TRUE),
(16,  6,  8, 'Okay, I will settle it tonight. Sorry about that.',                                                                      '2026-09-14 14:02:00', TRUE),
(23,  4, 15, 'Hello, I need to cancel my October 5 booking because of a change in plans.',                                             '2026-10-01 09:20:00', TRUE),
( 4, 23, 15, 'Sorry to hear that. A 10 percent cancellation fee applies and the rest will be refunded.',                               '2026-10-01 10:05:00', TRUE),
(24,  2, 16, 'Hi, I need to cancel due to a family emergency. Can I get a refund?',                                                    '2026-10-05 15:10:00', TRUE),
( 2, 24, 16, 'Sorry about that. We will process your refund with a small cancellation fee.',                                           '2026-10-05 16:00:00', TRUE),
(27,  7, 19, 'My flight was cancelled. Can I cancel Villa 2 and request a refund?',                                                    '2026-10-10 11:05:00', TRUE),
( 7, 27, 19, 'Understood, I have approved your refund request. Processing may take a few days.',                                       '2026-10-11 09:15:00', TRUE),
( 9,  4, 23, 'Good evening! Is firewood available for the fireplace?',                                                                 '2026-10-23 16:40:00', TRUE),
( 4,  9, 23, 'Yes, firewood is in the cabin storage and free for guests.',                                                             '2026-10-23 17:00:00', TRUE),
(10,  1, 24, 'Hi! The microwave in Studio 3 is not heating.',                                                                          '2026-10-24 19:20:00', TRUE),
( 1, 10, 24, 'We will have our staff check it tomorrow morning. Sorry for the trouble.',                                               '2026-10-24 19:45:00', FALSE),
(11,  6, 25, 'Hi, we just arrived. Where can we park?',                                                                                '2026-10-25 14:30:00', TRUE),
( 6, 11, 25, 'Welcome! Guest parking is at basement level B1, slot 14.',                                                               '2026-10-25 14:35:00', FALSE),
(12,  2, 26, 'Is it okay to arrive late around 9 PM on October 29?',                                                                   '2026-10-25 10:15:00', FALSE),
(13,  7, 27, 'Hello! Can we use the pool at night?',                                                                                   '2026-10-22 20:15:00', TRUE),
( 7, 13, 27, 'The pool is open until 10 PM. Please keep noise low after 9.',                                                           '2026-10-22 20:40:00', TRUE),
(16,  7, 30, 'Is Loft 3 good for 4 persons? We might bring a friend.',                                                                 '2026-10-24 12:00:00', TRUE),
( 7, 16, 30, 'Loft 3 sleeps up to 4, so that works.',                                                                                  '2026-10-24 12:30:00', FALSE),
(17,  5, 31, 'Hi, can we add a baby crib for December 24?',                                                                            '2026-10-21 18:00:00', TRUE),
( 5, 17, 31, 'Sure, we can provide one at no extra charge.',                                                                           '2026-10-21 18:40:00', TRUE),
( 8,  6, 33, 'Hi Antonio, I am interested in Unit 6B for November 20.',                                                                '2026-10-22 09:00:00', TRUE),
(14,  8, NULL, 'Hi, do you accept long-term stays at the farm stay?',                                                                  '2026-10-23 10:00:00', TRUE),
( 8, 14, NULL, 'Yes, monthly stays are possible. Please send your preferred dates.',                                                   '2026-10-23 11:20:00', FALSE);

-- ------------------------------------------------------------
-- 8. Items (34). unit_id is NULL for shared items, and a unit
--    always belongs to the same property as the item.
-- ------------------------------------------------------------
INSERT INTO Items (property_id, unit_id, item_name, item_type, quantity, unit_price, item_condition) VALUES
( 1, NULL, 'Fire Extinguisher',           'equipment',   4,  1200.00, 'good'),
( 1,    1, 'Split-type Aircon',           'equipment',   1, 28000.00, 'good'),
( 1,    1, 'Bath Towels',                 'consumable',  6,   250.00, 'good'),
( 1,    2, '43-inch Smart TV',            'equipment',   1, 18500.00, 'good'),
( 1,    3, 'Queen Bed Mattress',          'equipment',   1, 12000.00, 'good'),
( 1, NULL, 'Cleaning Supplies Set',       'consumable', 10,   350.00, 'good'),
( 2, NULL, 'CCTV Camera',                 'equipment',   4,  3500.00, 'good'),
( 2,    5, 'Refrigerator',                'equipment',   1, 15000.00, 'good'),
( 2,    6, 'Electric Fan',                'equipment',   2,  1800.00, 'good'),
( 2,    8, 'Water Heater',                'equipment',   1,  6500.00, 'maintenance'),
( 3,    9, 'Outdoor Grill Set',           'equipment',   1,  7500.00, 'good'),
( 3,    9, 'Kayak',                       'equipment',   2,  9000.00, 'damaged'),
( 3, NULL, 'Life Vest',                   'equipment',   8,   650.00, 'good'),
( 4,   10, 'Electric Blanket',            'equipment',   4,  1200.00, 'good'),
( 4, NULL, 'Firewood Bundle',             'consumable', 30,   120.00, 'good'),
( 4,   12, 'Wooden Dining Set',           'equipment',   1, 14000.00, 'good'),
( 5,   13, 'Induction Cooker',            'equipment',   1,  2800.00, 'good'),
( 5,   15, 'Microwave Oven',              'equipment',   1,  4200.00, 'maintenance'),
( 5, NULL, 'Laundry Detergent',           'consumable', 12,   180.00, 'good'),
( 6, NULL, 'Generator Set',               'equipment',   1, 35000.00, 'good'),
( 6,   17, 'Washing Machine',             'equipment',   1, 16500.00, 'good'),
( 7,   18, 'Sofa Bed',                    'equipment',   1, 14500.00, 'good'),
( 7,   19, 'Bedsheet Set',                'consumable',  8,   650.00, 'good'),
( 7, NULL, 'Key Card Reader',             'equipment',   2,  8800.00, 'good'),
( 8,   22, 'Pool Towels',                 'consumable', 10,   300.00, 'good'),
( 8, NULL, 'Pool Cleaning Chemicals',     'consumable',  5,   950.00, 'good'),
( 9,   25, 'Study Table',                 'equipment',   1,  2200.00, 'good'),
( 9, NULL, 'Water Dispenser',             'equipment',   2,  5800.00, 'maintenance'),
(10,   29, 'Bamboo Furniture Set',        'equipment',   1,  9800.00, 'good'),
(10, NULL, 'Garden Tools Set',            'equipment',   3,  1500.00, 'good'),
(11,   30, 'Wall Mirror',                 'equipment',   1,  2400.00, 'good'),
(11,   31, 'Hair Dryer',                  'equipment',   1,  1500.00, 'missing'),
(12,   33, 'Dining Table Set',            'equipment',   1, 18000.00, 'good'),
(12, NULL, 'Garage Gate Motor',           'equipment',   1, 12500.00, 'damaged');

-- ------------------------------------------------------------
-- 9. Maintenance (25). unit_id NULL = shared area, item_id NULL = no specific item.
-- ------------------------------------------------------------
INSERT INTO Maintenance (property_id, unit_id, item_id, maintenance_description, maintenance_cost, maintenance_status, started_at, completed_at) VALUES
( 2,    8, 10,   'Replace faulty heating element of the water heater',      1800.00, 'in_progress', '2026-10-20 09:00:00', NULL),
( 9,   28, NULL, 'Repair leaking ceiling in Room 4',                        3500.00, 'in_progress', '2026-10-22 08:30:00', NULL),
( 5,   16, NULL, 'Repaint walls and replace door lock of Studio 4',         2800.00, 'in_progress', '2026-10-23 09:00:00', NULL),
( 9, NULL, 28,   'Service water dispensers and replace filters',             650.00, 'pending',     '2026-10-27 09:00:00', NULL),
(12, NULL, 34,   'Repair garage gate motor',                                4200.00, 'pending',     '2026-10-28 09:00:00', NULL),
( 3,    9, 12,   'Patch crack on kayak hull',                               1500.00, 'completed',   '2026-09-02 09:00:00', '2026-09-03 16:00:00'),
( 1,    1, 2,    'Aircon cleaning and freon refill',                        1800.00, 'completed',   '2026-08-25 09:00:00', '2026-08-25 12:30:00'),
( 1, NULL, NULL, 'Fix plumbing leak in the lobby',                          3200.00, 'completed',   '2026-09-10 08:00:00', '2026-09-11 15:00:00'),
( 4, NULL, NULL, 'Roof gutter cleaning',                                    1200.00, 'completed',   '2026-09-05 08:00:00', '2026-09-05 14:00:00'),
( 4,   10, NULL, 'Replace bathroom faucet in Cabin 1',                       950.00, 'completed',   '2026-08-28 10:00:00', '2026-08-28 12:00:00'),
( 7, NULL, 24,   'Reprogram key card readers',                              2000.00, 'completed',   '2026-09-18 09:00:00', '2026-09-18 13:00:00'),
( 7,   18, 22,   'Reupholster sofa bed',                                    3600.00, 'completed',   '2026-09-25 09:00:00', '2026-09-27 17:00:00'),
( 8, NULL, NULL, 'Pool pump servicing',                                     4500.00, 'completed',   '2026-09-08 08:00:00', '2026-09-09 16:00:00'),
( 8,   24, NULL, 'Fix low water pressure in Villa 3',                       1100.00, 'completed',   '2026-10-02 09:00:00', '2026-10-02 13:00:00'),
( 2, NULL, 7,    'Reposition and clean CCTV cameras',                        800.00, 'completed',   '2026-09-20 10:00:00', '2026-09-20 15:00:00'),
( 5,   14, NULL, 'Replace light bulb and power outlet in Studio 2',          450.00, 'completed',   '2026-10-14 09:00:00', '2026-10-14 11:30:00'),
( 6, NULL, 20,   'Generator oil change and testing',                       1700.00, 'completed',   '2026-09-01 08:00:00', '2026-09-01 12:00:00'),
( 6,   17, 21,   'Replace washing machine drain hose',                       600.00, 'completed',   '2026-10-08 09:00:00', '2026-10-08 11:00:00'),
(10, NULL, NULL, 'Grass cutting and tree trimming',                         2500.00, 'completed',   '2026-09-15 07:00:00', '2026-09-15 15:00:00'),
(10,   29, NULL, 'Repair bamboo roofing of the cottage',                    5200.00, 'completed',   '2026-08-18 08:00:00', '2026-08-20 17:00:00'),
(11,   31, 32,   'Replace missing hair dryer',                              1500.00, 'completed',   '2026-10-19 10:00:00', '2026-10-19 11:00:00'),
(11, NULL, NULL, 'Repaint hallway',                                         6800.00, 'in_progress', '2026-10-24 08:00:00', NULL),
(12,   33, 33,   'Re-glue loose dining table legs',                          700.00, 'completed',   '2026-10-12 09:00:00', '2026-10-12 12:00:00'),
( 3, NULL, 13,   'Replace worn life vests',                                 5200.00, 'pending',     '2026-11-02 09:00:00', NULL),
( 1,    4, NULL, 'Replace shower head and fix drainage in Unit 202',        1300.00, 'completed',   '2026-10-24 13:00:00', '2026-10-24 16:30:00');

-- ------------------------------------------------------------
-- 10. FinancialTransactions (ledger)
--     a) Income from every PAID payment (rental + damage fee)
--     b) Refunds that were PROCESSED (amount = payment - cancellation fee)
--     c) Maintenance expenses from COMPLETED maintenance
--     d) Supply expenses (entered manually, no payment_id)
-- ------------------------------------------------------------
INSERT INTO FinancialTransactions (property_id, payment_id, transaction_type, amount, transaction_date)
SELECT u.property_id,
       p.payment_id,
       CASE p.payment_type WHEN 'rental_payment' THEN 'rental_income' ELSE 'damage_fee_income' END,
       p.amount,
       p.payment_date
FROM Payments p
JOIN Bookings b ON b.booking_id = p.booking_id
JOIN Units u    ON u.unit_id = b.unit_id
WHERE p.payment_status = 'paid';

INSERT INTO FinancialTransactions (property_id, payment_id, transaction_type, amount, transaction_date)
SELECT u.property_id,
       p.payment_id,
       'refund',
       p.amount - r.cancellation_fee,
       r.processed_at
FROM Refunds r
JOIN Payments p ON p.payment_id = r.payment_id
JOIN Bookings b ON b.booking_id = p.booking_id
JOIN Units u    ON u.unit_id = b.unit_id
WHERE r.refund_status = 'processed';

INSERT INTO FinancialTransactions (property_id, payment_id, transaction_type, amount, transaction_date)
SELECT m.property_id, NULL, 'maintenance_expense', m.maintenance_cost, m.completed_at
FROM Maintenance m
WHERE m.maintenance_status = 'completed' AND m.maintenance_cost > 0;

INSERT INTO FinancialTransactions (property_id, payment_id, transaction_type, amount, transaction_date) VALUES
( 1, NULL, 'supply_expense', 2200.00, '2026-08-30 10:00:00'),
( 2, NULL, 'supply_expense', 1800.00, '2026-09-02 10:30:00'),
( 4, NULL, 'supply_expense', 3600.00, '2026-09-10 09:00:00'),
( 5, NULL, 'supply_expense',  950.00, '2026-09-18 11:00:00'),
( 7, NULL, 'supply_expense', 2400.00, '2026-10-01 10:00:00'),
( 8, NULL, 'supply_expense', 2100.00, '2026-10-05 14:00:00'),
( 9, NULL, 'supply_expense', 1500.00, '2026-10-10 09:30:00'),
(11, NULL, 'supply_expense', 1250.00, '2026-10-12 15:00:00'),
( 3, NULL, 'supply_expense', 1900.00, '2026-10-15 10:00:00'),
( 6, NULL, 'supply_expense', 1600.00, '2026-10-18 13:00:00');

