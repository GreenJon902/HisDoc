from datetime import datetime, timedelta
from random import randint, random, choice

event_text_file = "./event_text.txt"
tags_text_file = "./tag_text.txt"
details_file = "./details.txt"
changelogs_file = "./changelogs.txt"
date_c_max_diff = 100
min_date = 100
max_date = 10000
eventeventrelation_val = 0.05
eventeventrelation_step_val = (1, 50)
eventtagrelation_val = 0.25
eventpersonrelation_val = 0.03
tagColMin = 0
tagColMax = 16581375

persons = (
        ('mc', '16ad067c-2be5-44e3-8218-58ba4ffba574'),
        ('mc', 'a7ddc940-7137-46b4-af8d-b30e3b64af03'),
        ('mc', '0acde9a4-cdc4-474b-8612-09c3020597af'),
        ('mc', 'e017d6cd-8265-4641-ab4f-206f1000aa34'),
        ('mc', '6b88f6eb-94cd-40d4-ae9d-58ff1a5a01dc'),
        ('misc', 'jackaboi1'),
        ('mc', '0dbffb6c-6165-40e4-b0f6-0fab4dcd5511'),
        ('mc', '0f549ef4-000b-4a9a-8fd2-2c3e7044ea54'),
        ('mc', '1b10c2a2-540c-4aa5-89fe-fd9c788e89b1'),
        ('mc', '2b20fb92-e115-41b3-9bd0-f2df259e85a9'),
        ('mc', '2b53145c-9f1c-4bb1-bfd0-a1d1b9f8af91'),
        ('mc', '2d2b43d8-b08c-4b95-86a4-9fa612b1c502'),
        ('mc', '2dea4bc2-d7cd-4b36-8d28-25223f11b79e'),
        ('mc', '3be062be-dfcf-4958-a8be-23bf40680a20'),
        ('mc', '3d7400e7-c0e8-4d1a-b669-3e2c2079beac'),
        ('mc', '4c3a51c1-c6b4-43f1-9894-77118b5685e9'),
        ('mc', '6a482beb-7b13-411e-b5c3-31f57aa9b5c7'),
        ('mc', '6b54dfbc-582b-4c65-9261-b01068030f13'),
        ('mc', '6daeb95a-babc-4efc-b7a9-3392617c08ae'),
        ('mc', '6e5417be-2544-465b-9ab0-409c9d468350'),
        ('mc', '6ff489f9-35bb-47ed-861a-efe23a99c137'),
        ('mc', '7d0b7161-bddd-4e3e-8010-7862db1df8cc'),
        ('mc', '7f534e88-376f-4921-8f65-3b7fad69df6c'),
        ('mc', '8c9a6a4a-d1ee-4b05-bbc4-289915616282'),
        ('mc', '9e14b381-e242-4ace-a446-e98caa3647e0'),
        ('mc', '17a87b0b-72f9-438c-8073-1104c02c6e75'),
        ('mc', '19f49c90-412a-40a5-9e2d-0c517a43addc'),
        ('mc', '20ec8e9d-2e26-4274-959b-91203016f8e3'),
        ('mc', '35eb8378-92f2-4b72-802a-8b2a54317047'),
        ('mc', '49c86d4f-15a2-4652-88b4-656d806f26a3'),
        ('mc', '55f7fb2c-d6ff-499d-90c9-de4869fa7517'),
        ('mc', '074a7abb-a28c-4842-84e7-9db5b883e14b'),
        ('mc', '77cedf03-fcbe-42ca-a0bb-7259c7bc1c0b'),
        ('mc', '79c286f4-8bf0-4f78-b5f1-6f9f53baaf4a'),
        ('mc', '86f5d3d8-0d4b-4230-9852-77a40baf39bd'),
        ('mc', '87fc5b25-4ad7-4bfa-95f8-9f54d97395c0'),
        ('mc', '91c946c0-dd54-45c0-8f4b-ea8298339b31'),
        ('mc', '143f9a48-d052-4350-bcae-ac3117dae93f'),
        ('mc', '255a2dad-fd9b-4fa1-83fa-b512bedd816e'),
        ('mc', '521c845c-14cc-4658-aa06-99fc1f7eb9dd'),
        ('mc', '676c408d-0d2e-4f7f-aa52-5844233b7d9c'),
        ('mc', '771de228-6df3-4c3d-b6d6-999f6b1b4035'),
        ('mc', '781fcc1a-6d8c-4dcc-94ef-94f0d64a1d1d'),
        ('mc', '976d36dd-d760-47bb-b1ab-c109ffa1d309'),
        ('mc', '4775fba8-f65d-46dd-b0f6-aa8320045316'),
        ('mc', '5299efcb-d9ac-41fd-b5e3-ec657e9ffadf'),
        ('mc', '6255ad99-39b8-43c0-bed4-ba44aa082b00'),
        ('mc', '9799a8b5-8fc8-4eaf-b09f-33f533bb68e8'),
        ('mc', '9954eccd-85d9-4c3f-88aa-0f26ec0548eb'),
        ('mc', '16141d63-a0a0-4522-8f5d-4f201a54306b'),
        ('mc', '42005aab-1623-4bab-9a9e-c942acde2810'),
        ('mc', '54496d06-edc8-44cb-a7db-7b0f514c3559'),
        ('mc', '87388cc0-fb4f-4e0a-94e4-de821beff903'),
        ('mc', '89693e70-bc94-4778-a185-3aea72c39c0f'),
        ('mc', '90441fca-cc63-46cd-a755-45bbc8aff2c2'),
        ('mc', '00820254-b582-4d50-bcf4-d2281d7b113a'),
        ('mc', '911866b2-fcd5-40c0-82c0-da34d52f4bc1'),
        ('mc', '4832584d-299d-496e-aa5a-3870ef817927'),
        ('mc', '13160433-4e30-4b0b-9721-41eead017c96'),
        ('mc', '36557293-12a8-4bd7-8313-8d07b0f1afbf'),
        ('mc', '45970799-5f21-453b-95d9-dbef8d667d89'),
        ('mc', '89404243-d4aa-44fb-bb0b-60ebc3a88abc'),
        ('mc', '96417818-1d0f-4e69-836a-721a9a7fc002'),
        ('mc', 'a4ec3830-6427-48d4-92fc-cb462ec7eb4e'),
        ('mc', 'a6f2f5da-5773-4432-b7b4-8ec0b34a104a'),
        ('mc', 'a46ac901-1c79-4b70-be60-6190005806b1'),
        ('mc', 'a5270a3d-9066-41c0-91e1-eb38fe6fc9da'),
        ('mc', 'b0c39bab-f2e7-4db7-bced-9f2e67439248'),
        ('mc', 'b8d53700-0a48-4a40-a523-46edf3f6ef37'),
        ('mc', 'b93b5bd7-234c-4f48-a139-9b0fffec9089'),
        ('mc', 'b428ed8b-8f47-48be-a799-15f892f86c0d'),
        ('mc', 'b890bf8a-f26b-4d09-8668-1c750c442568'),
        ('mc', 'b3687747-8a89-4eec-9262-a360f4b04b8b'),
        ('mc', 'be72b759-019a-40ca-9910-411a573c5262'),
        ('mc', 'c67c9ede-8556-4c96-822e-253837d94504'),
        ('mc', 'c70f5358-7d00-406f-979a-fde5ce3bb7c6'),
        ('mc', 'c1377ecc-0037-4e80-9c53-2480214dfbfb'),
        ('mc', 'cdd395a1-76f5-4e73-9580-c0384cd76c28'),
        ('mc', 'ce171539-ee3f-4fa9-9c02-cdd7f0481e88'),
        ('mc', 'd6a1c81e-b7b6-4afa-b579-dcb521f542ab'),
        ('mc', 'd8d85203-3391-4675-b2fc-9762e3ca2add'),
        ('mc', 'd9192f75-63ff-426b-8347-8532ad6655a8'),
        ('mc', 'e00a494b-47bb-4561-93b7-1f463bf0981e'),
        ('mc', 'e78fc65c-219a-4c24-8efa-6c7a9cff7b9e'),
        ('mc', 'e0201fe2-ec7b-4149-bd2d-0dc10b094c72'),
        ('mc', 'e461c785-2c29-41bc-83c1-7989b1abd761'),
        ('mc', 'ea4e1ef9-2aba-4dd7-a7ba-b5983de3d873'),
        ('mc', 'f025cbcf-d644-47f2-accf-6c0503f74c3e'),
        ('mc', 'fa5816f1-da87-4a08-89ce-137abf3e3573'),
        ('mc', 'fbd30704-b81a-4ec7-b008-def9b6e92683'),
        ('mc', 'fd3ed3ef-372e-49d8-a2c4-b774a092dec9'),
        ('mc', 'feeeed38-e869-4233-9c67-28e6f89c2234')
)

out = open("./out.sql", "w")


def parse_text_info(path):
    texts = open(path, "r").read().replace("?", "").replace("'", "").split("\n")  # Fixes characters that would break sql
    i = 0
    usedNames = []
    while i < len(texts):
        if texts[i] in usedNames:
            print("Tried to resuse name \"" + texts[i] + "\"")
        else:
            yield texts[i], texts[i + 1]
            usedNames.append(texts[i])
        assert texts[i + 2] == "", "line should be empty at " + str(i)
        i += 3


event_texts = list(parse_text_info(event_text_file))
tags = list(parse_text_info(tags_text_file))
details = list(open(details_file, "r").read().split("\n"))
changelogs = list(open(changelogs_file, "r").read().split("\n"))


def make_event_list():
    c_dates = ("INSERT INTO {prefix}Event (eid, name, eventDateType, eventDate1, eventDateUnits, eventDateDiff, "
               "postedDate, description, postedPid, details)"
               "VALUES \n")
    r_dates = ("INSERT INTO {prefix}Event (eid, name, eventDateType, eventDate1, eventDate2, "
               "postedDate, description, postedPid, details)"
               "VALUES \n")

    eid = 1
    for event_text in event_texts:
        name = event_text[0]
        description = event_text[1]
        date1 = randint(min_date, max_date)
        postedDate = randint(min_date, max_date)
        postedPid = randint(0, len(persons))
        if postedPid == 0:
            postedPid = "NULL"
        detail = "'" + choice(details) + "'" if randint(0, 1) == 1 else "NULL"

        if randint(0, 1) == 0:  # C Date
            date_units = ["d", "h", "m"][randint(0, 2)]
            date_diff = randint(0, date_c_max_diff)

            c_dates += f"({eid}, '{name}', 'c', {date1}, '{date_units}', {date_diff}, {postedDate}, '{description}', {postedPid}, {detail}), \n"
        else:  # R Date
            date2 = (date1 + randint(min_date, max_date))

            r_dates += f"({eid}, '{name}', 'r', {date1}, {date2}, {postedDate}, '{description}', {postedPid}, {detail}), \n"

        eid += 1

    c_dates = c_dates.rstrip(", \n")
    c_dates += ";\n"
    out.write(c_dates)

    r_dates = r_dates.rstrip(", \n")
    r_dates += ";\n"
    out.write(r_dates)


def make_person_list():
    string = "INSERT INTO {prefix}Person (pid, personType, personData) VALUES \n"

    for n, person in enumerate(persons):
        string += f"({n + 1}, '{person[0]}', '{person[1]}'), \n"

    string = string.rstrip(", \n")
    string += ";\n"
    out.write(string)


def make_tag_list():
    string = "INSERT INTO {prefix}Tag (tid, name, description, color) VALUES \n"

    for n, tag in enumerate(tags):
        string += f"({n + 1}, '{tag[0]}', '{tag[1]}', {randint(tagColMin, tagColMax)}), \n"

    string = string.rstrip(", \n")
    string += ";\n"
    out.write(string)


def make_event_event_relation():
    string = "INSERT INTO {prefix}EventEventRelation (eid1, eid2) VALUES \n"

    done = []  # To stop the same thing but the other way round

    for n1 in range(len(event_texts) - 1):
        for n2 in range(0, len(event_texts) - 1, randint(*eventeventrelation_step_val)):
            if (n2, n1) not in done and n1 != n2:
                if random() < eventeventrelation_val:
                    string += f"({n1 + 1}, {n2 + 1}), \n"
                    done.append((n1, n2))
        print(n1 + 1, "/", len(event_texts) - 1)

    string = string.rstrip(", \n")
    string += ";\n"
    out.write(string)


def make_event_person_relation():
    string = "INSERT INTO {prefix}EventPersonRelation (eid, pid) VALUES \n"

    done = []  # To stop the same thing but the other way round

    for n1 in range(len(event_texts) - 1):
        for n2 in range(len(persons) - 1):
            if (n2, n1) not in done:
                if random() < eventpersonrelation_val:
                    string += f"({n1 + 1}, {n2 + 1}), \n"
                    done.append((n1, n2))

    string = string.rstrip(", \n")
    string += ";\n"
    out.write(string)


def make_event_tag_relation():
    string = "INSERT INTO {prefix}EventTagRelation (eid, tid) VALUES \n"

    done = []  # To stop the same thing but the other way round

    for n1 in range(len(event_texts) - 1):
        for n2 in range(len(tags) - 1):
            if (n2, n1) not in done:
                if random() < eventtagrelation_val:
                    string += f"({n1 + 1}, {n2 + 1}), \n"
                    done.append((n1, n2))

    string = string.rstrip(", \n")
    string += ";\n"
    out.write(string)


def make_changelogs():
    string = "INSERT INTO {prefix}ChangeLog (eid, description, authorPid, date) VALUES \n"

    for i in range(len(event_texts) - 1):
        count = randint(0, len(changelogs))
        for _ in range(count - 1):
            string += f"({i + 1}, '{choice(changelogs)}', '{randint(1, len(persons))}', {randint(min_date, max_date)}), \n"

    string = string.rstrip(", \n")
    string += ";\n"
    out.write(string)


make_person_list()
print("Made persons")
make_event_list()
print("Made events")
make_tag_list()
print("Made tags")
make_event_event_relation()
print("Made event event")
make_event_tag_relation()
print("Made event tag")
make_event_person_relation()
print("Made event person")
make_changelogs()
print("Making changelogs")
out.close()
