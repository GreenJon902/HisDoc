### Records
___Info - All the information needed to render a page (changeInfo doesn't have a page or a link).
___Link - All the information needed to render a link to a page

### stuff which needs to be documented
set means unordered,
lists are ordered, e.g. recent events are ordered from most recent to last recent
all lists should also be made unmodifiable inside the record initializer

tests inside apgebuilder mostly jsut check taht they run.
any widget tests will use the root pagebuilder so the html works

css files should be layed out identifially.

A "Person" refers to someone in the timeline
A "User" refers to someone using the website
A "Session" refers to someone adding events who is linked to an ingame account


Use getString for getting ips from addresses

### Person info (string version, same as whats stored in sql)
so like as names and stuffs change we would like to store uuid and get from api.
So person info can be a prefixed uuid, e.g. `MC|0dbffb6c-6165-40e4-b0f6-0fab4dcd5511` to say 
that this is a minecraft account with that uuid, so the program can then load the name afterwards.
Or just put any random text in there (obvi excluding prefix) to say its not linked to anything, e.g. `Dave123`

# Dates
So the issue is (and this goes for both date types), take Jimme here, Jimme lives in Bangladesh (GMT+06:00).
Jimme uses hisDoc (Which is running under GMT for example) to log an event on the 05/05/0005 02:45 Bangladesh Time.
However since time is cropped out, we log an event on 05/05/0005. But since timezones, 02:45 should become 08:45 GMT
on the day before? But we don't know the time, do we?
Since the current system assumes the time to be 00:00, it then would default to the day before, which works
for this example, but if Jimme had 05/05/0005 20:00 then hisDoc should would crop the time of and it would
become the day before again.

Therefor the date system becomes even more convoluted, we will just talk about singular dates here, not ranges.  
We store datetimes as longs since the unix epoch (1 January 1970, 00:00:00) but in their timezone. If we do not (or might not) store minutes or below,
we also store an offset, in minutes between the time this datetime is and UTC. 
This means we can  say also the exact start and end possible dates of an event without worrying about
day overlaps

# Prepared statements and sql safety
for integers, we can just replace which also helps incase info has to be repeated.
However for anything else, we theoretically only actually want to have it once anyway (especisally inserts),
so use ? instead

# versioning
The version in pom.xml is the version of the last release. E.g. after v1.0.1, the version will say that until v1.0.2 releases.