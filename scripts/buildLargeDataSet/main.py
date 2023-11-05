# TODO: Changlosg

from datetime import datetime, timedelta
from random import randint, random

event_text_file = "./event_text.txt"
date_c_max_diff = 100
max_date = datetime.now()
max_date_days_since_min = 1000
eventeventrelation_val = 0.01
eventeventrelation_step_val = (1, 100)
eventtagrelation_val = 0.5
eventuserrelation_val = 0.5

users = (('mc', 'a6f2f5da-5773-4432-b7b4-8ec0b34a104a'),
         ('mc', '86f5d3d8-0d4b-4230-9852-77a40baf39bd'),
         ('mc', '0dbffb6c-6165-40e4-b0f6-0fab4dcd5511'),
         ('mc', '16ad067c-2be5-44e3-8218-58ba4ffba574'),
         ('mc', 'a7ddc940-7137-46b4-af8d-b30e3b64af03'),
         ('mc', '0acde9a4-cdc4-474b-8612-09c3020597af'),
         ('mc', 'e017d6cd-8265-4641-ab4f-206f1000aa34'),
         ('mc', '6b88f6eb-94cd-40d4-ae9d-58ff1a5a01dc'),
         ('mc', '6b54dfbc-582b-4c65-9261-b01068030f13'),
         ('misc', 'jackaboi1'),
         ('mc', 'b93b5bd7-234c-4f48-a139-9b0fffec9089'),
         ('mc', '6a482beb-7b13-411e-b5c3-31f57aa9b5c7'))

tags = (('Base', 'An event related to someone''s base', 65280),
        ('Mining', 'An event related to mining activities', 16711680),
        ('Exploration', 'An event related to exploring new areas', 65535),
        ('Farming', 'An event related to farming and agriculture', 16777215),
        ('PvP', 'An event related to player vs. player combat', 255),
        ('Construction', 'An event related to building structures', 16776960),
        ('Trading', 'An event related to player trading', 16711935),
        ('Events', 'An event related to in-game events', 8323327),
        ('Mob Farm', 'An event related to mob farming', 16777214),
        ('Quest', 'An event related to in-game quests and challenges', 16764108))

out = open("./out.sql", "w")


def parse_event_text_info():
    texts = open(event_text_file, "r").read().replace("?", "").replace("'", "").split("\n")  # TODO: Find fix for special characters
    i = 0
    while i < len(texts):
        yield texts[i], texts[i + 1]
        assert texts[i + 2] == "", "line should be empty at " + str(i)
        i += 3


def format_timestamp(date):
    return f"TIMESTAMP('{date.strftime('%Y-%m-%d')}', '{date.strftime('%H:%M:%S')}')"


event_texts = list(parse_event_text_info())


def make_event_list():
    c_dates = ("INSERT INTO {prefix}Event (eid, name, eventDateType, eventDate1, eventDatePrecision, eventDateDiff, "
               "eventDateDiffType, postedDate, description, postedUid)"
               "VALUES \n")
    b_dates = ("INSERT INTO {prefix}Event (eid, name, eventDateType, eventDate1, eventDate2, "
               "postedDate, description, postedUid)"
               "VALUES \n")

    eid = 1
    for event_text in event_texts:
        name = event_text[0]
        description = event_text[1]
        date1 = (max_date - timedelta(days=randint(0, max_date_days_since_min)))
        postedDate = (max_date - timedelta(days=randint(0, max_date_days_since_min)))
        postedUid = randint(1, len(users))

        if randint(0, 1) == 0:  # C Date
            date_precision = ["d", "h", "m"][randint(0, 2)]
            date_diff = randint(0, date_c_max_diff)
            date_diff_type = ["d", "h", "m"][randint(0, 2)]

            c_dates += f"({eid}, '{name}', 'c', {format_timestamp(date1)}, '{date_precision}', {date_diff}, '{date_diff_type}', {format_timestamp(postedDate)}, '{description}', {postedUid}), \n"
        else:  # B Date
            date2 = (date1 + timedelta(days=randint(0, max_date_days_since_min)))

            b_dates += f"({eid}, '{name}', 'b', {format_timestamp(date1)}, {format_timestamp(date2)}, {format_timestamp(postedDate)}, '{description}', {postedUid}), \n"

        eid += 1

    c_dates = c_dates.rstrip(", \n")
    c_dates += ";\n"
    out.write(c_dates)

    b_dates = b_dates.rstrip(", \n")
    b_dates += ";\n"
    out.write(b_dates)


def make_user_list():
    string = "INSERT INTO {prefix}User (uid, userType, userData) VALUES \n"

    for n, user in enumerate(users):
        string += f"({n + 1}, '{user[0]}', '{user[1]}'), \n"

    string = string.rstrip(", \n")
    string += ";\n"
    out.write(string)


def make_tag_list():
    string = "INSERT INTO {prefix}Tag (tid, name, description, color) VALUES \n"

    for n, tag in enumerate(tags):
        string += f"({n + 1}, '{tag[0]}', '{tag[1]}', {tag[2]}), \n"

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


def make_event_user_relation():
    string = "INSERT INTO {prefix}EventUserRelation (eid, uid) VALUES \n"

    done = []  # To stop the same thing but the other way round

    for n1 in range(len(event_texts) - 1):
        for n2 in range(len(users) - 1):
            if (n2, n1) not in done:
                if random() < eventuserrelation_val:
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


make_user_list()
print("Made users")
make_event_list()
print("Made events")
make_tag_list()
print("Made tags")
make_event_event_relation()
print("Made event event")
make_event_tag_relation()
print("Made event tag")
make_event_user_relation()
print("Made event user")
out.close()
