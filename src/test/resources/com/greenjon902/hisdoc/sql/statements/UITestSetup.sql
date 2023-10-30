INSERT INTO {prefix}Tag (tid, name, description, color)
VALUES
    (1, 'Base', 'An event related to someone''s base', 65280), -- Green (0x00FF00)
    (2, 'Mining', 'An event related to mining activities', 16711680), -- Red (0xFF0000)
    (3, 'Exploration', 'An event related to exploring new areas', 65535), -- Yellow (0xFFFF00)
    (4, 'Farming', 'An event related to farming and agriculture', 16777215), -- White (0xFFFFFF)
    (5, 'PvP', 'An event related to player vs. player combat', 255), -- Blue (0x0000FF)
    (6, 'Construction', 'An event related to building structures', 16776960), -- Orange (0xFFA500)
    (7, 'Trading', 'An event related to player trading', 16711935), -- Purple (0xFF00FF)
    (8, 'Events', 'An event related to in-game events', 8323327), -- Cyan (0x7FFFD4)
    (9, 'Mob Farm', 'An event related to mob farming', 16777214), -- Light Gray (0xC0C0C0)
    (10, 'Quest', 'An event related to in-game quests and challenges', 16764108); -- Gold (0xFFD700)


INSERT INTO {prefix}User (uid, userInfo)
VALUES
    (1, 'MC|a6f2f5da-5773-4432-b7b4-8ec0b34a104a'),
    (2, 'MC|86f5d3d8-0d4b-4230-9852-77a40baf39bd'),
    (3, 'MC|0dbffb6c-6165-40e4-b0f6-0fab4dcd5511'),
    (4, 'MC|16ad067c-2be5-44e3-8218-58ba4ffba574'),
    (5, 'MC|a7ddc940-7137-46b4-af8d-b30e3b64af03'),
    (6, 'MC|0acde9a4-cdc4-474b-8612-09c3020597af'),
    (7, 'MC|e017d6cd-8265-4641-ab4f-206f1000aa34'),
    (8, 'MC|6b88f6eb-94cd-40d4-ae9d-58ff1a5a01dc'),
    (9, 'MC|6b54dfbc-582b-4c65-9261-b01068030f13'),
    (10, 'jackaboi1'),
    (11, 'MC|b93b5bd7-234c-4f48-a139-9b0fffec9089'),
    (12, 'MC|6a482beb-7b13-411e-b5c3-31f57aa9b5c7');


INSERT INTO {prefix}Event (eid, name, eventDateType, eventDate1, eventDatePrecision, eventDateDiff, eventDateDiffType, postedDate, description, postedUid)
VALUES
    (1, 'Mining Expedition', 'c', TIMESTAMP('2023-01-15', '10:30:00'), 'm', 3, 'h', TIMESTAMP('2023-01-16', '08:45:00'), 'A group of miners explored a deep cave system.', 3),
    (2, 'Base Construction', 'c', TIMESTAMP('2023-02-02', '14:15:00'), 'm', 4, 'm', NULL, 'Started building a new base near the river.', 4),
    (3, 'Village Raid', 'c', TIMESTAMP('2023-02-10', '19:00:00'), 'h', 5, 'd', TIMESTAMP('2023-02-11', '09:30:00'), 'Village was attacked by hostile mobs.', NULL),
    (4, 'Nether Portal Activation', 'c', TIMESTAMP('2023-03-05', '22:20:00'), 'm', 0, 'h', NULL, 'Successfully activated the Nether portal.', NULL),
    (5, 'Crop Harvest', 'c', TIMESTAMP('2023-03-10', '16:45:00'), 'd', 0, 'h', TIMESTAMP('2023-03-11', '07:10:00'), 'Harvested wheat, carrots, and potatoes.', 5),
    (6, 'Dungeon Exploration', 'c', TIMESTAMP('2023-04-02', '11:00:00'), 'd', 2, 'h', NULL, 'Explored a nearby dungeon and found loot.', NULL),
    (7, 'Enderman Encounter', 'c', TIMESTAMP('2023-04-15', '23:30:00'), 'd', 4, 'h', TIMESTAMP('2023-04-16', '08:20:00'), 'Encountered and defeated an Enderman.', 4),
    (8, 'Nether Fortress Raid', 'c', TIMESTAMP('2023-05-08', '13:45:00'), 'd', 0, 'h', NULL, 'Conquered a Nether Fortress for resources.', 11),
    (9, 'Ocean Monument Expedition', 'c', TIMESTAMP('2023-05-20', '15:15:00'), 'd', 5, 'm', NULL, 'Explored an ocean monument for treasure.', 12),
    (10, 'Cave-In Disaster', 'c', TIMESTAMP('2023-06-10', '09:10:00'), 'd', 0, 'h', TIMESTAMP('2023-06-11', '11:30:00'), 'A cave-in trapped some miners; rescue mission initiated.', NULL),
    (11, 'Treehouse Construction', 'c', TIMESTAMP('2023-07-01', '17:00:00'), 'd', 6, 'h', NULL, 'Started building a treehouse in the forest.', NULL),
    (12, 'Horse Taming', 'c', TIMESTAMP('2023-07-15', '08:40:00'), 'd', 0, 'h', TIMESTAMP('2023-07-16', '10:25:00'), 'Successfully tamed a wild horse.', NULL),
    (13, 'Underwater Ruin Discovery', 'c', TIMESTAMP('2023-08-05', '14:20:00'), 'd', 0, 'h', NULL, 'Found an underwater ruin and explored it.', 2),
    (14, 'Ender Dragon Defeat', 'c', TIMESTAMP('2023-09-03', '20:00:00'), 'm', 2, 'm', NULL, 'Defeated the Ender Dragon in the End dimension.', 4),
    (15, 'Enchanting Room Setup', 'c', TIMESTAMP('2023-09-20', '12:30:00'), 'h', 1, 'h', TIMESTAMP('2023-09-21', '09:45:00'), 'Set up an enchanting room in the base.', NULL),
    (16, 'Potion Brewing', 'c', TIMESTAMP('2023-10-12', '11:15:00'), 'h', 0, 'h', NULL, 'Brewed various potions for upcoming adventures.', 3),
    (17, 'Desert Temple Raid', 'c', TIMESTAMP('2023-10-28', '18:45:00'), 'h', 1, 'd', NULL, 'Explored a desert temple and collected treasures.', 5),
    (18, 'Villager Trading', 'c', TIMESTAMP('2023-11-10', '14:00:00'), 'h', 0, 'h', TIMESTAMP('2023-11-11', '10:10:00'), 'Engaged in trading with villagers.', 6);
INSERT INTO {prefix}Event (eid, name, eventDateType, eventDate1, eventDate2, postedDate, description, postedUid)
VALUES
    (19, 'Ender Pearl Hunt', 'b', TIMESTAMP('2023-11-25', '22:30:00'), DATE('2023-11-26'), TIMESTAMP('2023-12-09', '07:55:00'), 'Hunted Endermen for Ender Pearls.', 3),
    (20, 'Nether Hub Construction', 'b', TIMESTAMP('2023-12-08', '16:20:00'), DATE('2023-12-18'),  TIMESTAMP('2023-12-09', '07:55:00'), 'Built a Nether hub for fast travel.', 4),
    (21, 'Jungle Temple Discovery', 'b', TIMESTAMP('2024-01-05', '19:10:00'), DATE('2024-02-04'), NULL, 'Found a jungle temple and explored its traps.', 5),
    (22, 'Exploring the Mesa', 'b', TIMESTAMP('2024-01-20', '10:50:00'), DATE('2024-02-20'), NULL, 'Ventured into the mesa biome for unique resources.', 4),
    (23, 'Witch Hut Conquest', 'b', TIMESTAMP('2024-02-10', '21:30:00'), DATE('2024-03-15'), NULL, 'Conquered a witch hut and collected supplies.', 6),
    (24, 'Snow Golem Creation', 'b', TIMESTAMP('2024-02-25', '13:15:00'), DATE('2024-03-30'), TIMESTAMP('2024-02-26', '08:40:00'), 'Created snow golems for added protection.', 12),
    (25, 'Enderman Farm Setup', 'b', TIMESTAMP('2024-03-12', '12:25:00'), DATE('2025-09-12'), NULL, 'Set up an Enderman farm for XP and resources.', 4),
    (26, 'Mushroom Biome Expedition', 'b', TIMESTAMP('2024-03-28', '17:55:00'), DATE('2024-04-29'), NULL, 'Explored a mushroom biome for mycelium and mushrooms.', 10),
    (27, 'Cursed Villager Cure', 'b', TIMESTAMP('2024-04-15', '09:10:00'), DATE('2024-05-05'), TIMESTAMP('2024-04-16', '11:25:00'), 'Cured a cursed villager and got discounts.', 3),
    (28, 'Nether Wart Farming', 'b', TIMESTAMP('2024-05-03', '14:40:00'), DATE('2024-08-04'), NULL, 'Started farming Nether Warts for potion making.', NULL),
    (29, 'Ice Spikes Biome Discovery', 'b', TIMESTAMP('2024-05-20', '20:20:00'), DATE('2024-06-17'), NULL, 'Found an ice spikes biome for packed ice.', 5),
    (30, 'End Portal Activation', 'b', TIMESTAMP('2024-06-08', '08:10:00'), DATE('2024-06-21'), NULL, 'Activated the End Portal for the final journey.', 6);


INSERT INTO {prefix}EventTagRelation (eid, tid)
VALUES
    (1, 1), (1, 3), (1, 6),
    (2, 1), (2, 2), (2, 5), (2, 7),
    (3, 3), (3, 5), (3, 8),
    (4, 1), (4, 4), (4, 6), (4, 9),
    (5, 2), (5, 5), (5, 7),
    (6, 1), (6, 3), (6, 5), (6, 6), (6, 8),
    (7, 4), (7, 6), (7, 10),
    (10, 1), (10, 4), (10, 5), (10, 6), (10, 8),
    (11, 6), (11, 7), (11, 9),
    (12, 3), (12, 8),
    (13, 2), (13, 4), (13, 6), (13, 9),
    (14, 1), (14, 2), (14, 5), (14, 10),
    (15, 3), (15, 8),
    (17, 4), (17, 5), (17, 6),
    (18, 1), (18, 2), (18, 5), (18, 7),
    (19, 4), (19, 6),
    (20, 4), (20, 6), (20, 10),
    (21, 2), (21, 7), (21, 9),
    (22, 1), (22, 2), (22, 5), (22, 7), (22, 10),
    (23, 3), (23, 5), (23, 8),
    (24, 2), (24, 4), (24, 6), (24, 9),
    (25, 1), (25, 4), (25, 7),
    (26, 4), (26, 6), (26, 8),
    (27, 2), (27, 5), (27, 10),
    (28, 1), (28, 4), (28, 7), (28, 9);


INSERT INTO {prefix}EventUserRelation (eid, uid)
VALUES
    (1, 1), (1, 3),
    (2, 2), (2, 5),
    (4, 1), (4, 3), (4, 8),
    (5, 2), (5, 6), (5, 10),
    (6, 1), (6, 2), (6, 4), (6, 7),
    (7, 4), (7, 5), (7, 6), (7, 7), (7, 8),
    (10, 1), (10, 3), (10, 4),
    (11, 4), (11, 5),
    (12, 2), (12, 6),
    (13, 3), (13, 8), (13, 10),
    (14, 1), (14, 3), (14, 4),
    (15, 5), (15, 7),
    (17, 1), (17, 4),
    (18, 3), (18, 5), (18, 6),
    (19, 7), (19, 8),
    (20, 1), (20, 3), (20, 4), (20, 6), (20, 7),
    (21, 5), (21, 10),
    (23, 3), (23, 6), (23, 8), (23, 10),
    (24, 4), (24, 5), (24, 8),
    (25, 2), (25, 6), (25, 10),
    (26, 1), (26, 3), (26, 4),
    (27, 4), (27, 7),
    (29, 3), (29, 6), (29, 8);


INSERT INTO {prefix}EventEventRelation (eid1, eid2)
VALUES
    (1, 2), (1, 3), (1, 4),
    (2, 5),
    (3, 6), (3, 7),
    (4, 8), (4, 9),
    (5, 10), (5, 11), (5, 12), (5, 13),
    (6, 14), (6, 15), (6, 16),
    (7, 17),
    (8, 18),
    (9, 19), (9, 20),
    (10, 21), (10, 22),
    (11, 23),
    (12, 24),
    (13, 25),
    (14, 26),
    (15, 27),
    (16, 28),
    (17, 29),
    (18, 30);


INSERT INTO {prefix}ChangeLog (eid, authorUid, description, date)
VALUES
    (1, 1, 'Updated event details.', TIMESTAMP('2023-10-01', '14:30:00')),
    (1, 2, 'Added participant list.', TIMESTAMP('2023-10-02', '09:15:00')),
    (2, 3, 'Changed location.', TIMESTAMP('2023-10-03', '16:45:00')),
    (3, 4, 'Updated description.', TIMESTAMP('2023-10-04', '11:20:00')),
    (3, 5, 'Added images.', TIMESTAMP('2023-10-05', '13:10:00')),
    (4, 6, 'Updated event name.', TIMESTAMP('2023-10-06', '10:00:00')),
    (5, 7, 'Revised event goals.', TIMESTAMP('2023-10-07', '15:30:00')),
    (6, 8, 'Changed event type.', TIMESTAMP('2023-10-08', '08:45:00')),
    (6, 9, 'Added coordinates.', TIMESTAMP('2023-10-09', '12:55:00')),
    (6, 10, 'Updated description.', TIMESTAMP('2023-10-10', '14:20:00')),
    (7, 11, 'Updated event details.', TIMESTAMP('2023-10-11', '09:10:00')),
    (8, 12, 'Added participant list.', TIMESTAMP('2023-10-12', '11:30:00')),
    (9, 12, 'Changed location.', TIMESTAMP('2023-10-13', '16:15:00')),
    (10, 4, 'Updated description.', TIMESTAMP('2023-10-14', '10:40:00')),
    (11, 5, 'Added images.', TIMESTAMP('2023-10-15', '14:00:00')),
    (12, 3, 'Updated event name.', TIMESTAMP('2023-10-16', '12:25:00')),
    (13, 5, 'Revised event goals.', TIMESTAMP('2023-10-17', '13:55:00')),
    (14, 6, 'Changed event type.', TIMESTAMP('2023-10-18', '11:05:00')),
    (14, 4, 'Added coordinates.', TIMESTAMP('2023-10-19', '14:50:00')),
    (15, 6, 'Updated description.', TIMESTAMP('2023-10-20', '08:30:00')),
    (16, 5, 'Updated event details.', TIMESTAMP('2023-10-21', '12:00:00')),
    (17, 6, 'Added participant list.', TIMESTAMP('2023-10-22', '10:15:00')),
    (18, 3, 'Changed location.', TIMESTAMP('2023-10-23', '15:45:00')),
    (19, 8, 'Updated description.', TIMESTAMP('2023-10-24', '09:25:00')),
    (20, 11, 'Added images.', TIMESTAMP('2023-10-25', '13:30:00')),
    (21, 12, 'Updated event name.', TIMESTAMP('2023-10-26', '16:20:00')),
    (22, 12, 'Revised event goals.', TIMESTAMP('2023-10-27', '08:35:00')),
    (22, 12, 'Changed event type.', TIMESTAMP('2023-10-28', '11:55:00')),
    (23, 12, 'Added coordinates.', TIMESTAMP('2023-10-29', '14:40:00'));
