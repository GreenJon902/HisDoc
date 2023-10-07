SELECT {prefix}Tag.name as text FROM {prefix}Tag
UNION
SELECT {prefix}User.userInfo as text FROM {prefix}User