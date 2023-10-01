SELECT Tags.name as text FROM Tags
UNION
SELECT Users.userInfo as text FROM Users