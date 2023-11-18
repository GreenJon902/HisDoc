SELECT {prefix}Tag.name as text FROM {prefix}Tag
UNION
SELECT {prefix}Person.personInfo as text FROM {prefix}Person