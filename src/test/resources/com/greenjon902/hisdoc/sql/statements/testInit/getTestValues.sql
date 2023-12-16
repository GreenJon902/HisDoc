SELECT {prefix}Tag.name as text FROM {prefix}Tag
UNION
SELECT {prefix}Person.personData as text FROM {prefix}Person