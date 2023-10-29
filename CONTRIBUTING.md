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

### User info (string version, same as whats stored in sql)
so like as names and stuffs change we would like to store uuid and get from api.
So user info can be a prefixed uuid, e.g. `MC|0dbffb6c-6165-40e4-b0f6-0fab4dcd5511` to say 
that this is a minecraft account with that uuid, so the program can then load the name afterwards.
Or just put any random text in there (obvi excluding prefix) to say its not linked to anything, e.g. `Dave123`
