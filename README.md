# DiscordBot
Paragon Discord Bot, made by Harry 

### Functions
- Elo Checking with Agora.gg API

### Uses the following libraries:
JDA: http://home.dv8tion.net:8080/job/JDA/
JsonP: https://javaee.github.io/jsonp/

### How to deploy to servers
1. Clone repository
2. Go to /builds/newest/
3. Run app.jar with Discord Token as an argument

### How to add to servers
- Use Discord Permission calculator [here](https://discordapi.com/permissions.html)
- Visit this link, replace {bot-id} with your Client ID and {permissions} with those you want from above
- https://discordapp.com/oauth2/authorize?&client_id={bot-id}&scope=bot&permissions={permissions}

### Release Notes

#### Aug 16, 2017
- Renamed bot from DiscordBot to ParaBot
- Updated Elo function to match Agora.gg API v1 and Paragon's v.42 update
- Changed JSON parser from *org.json* to *javax.json*

#### Prior to May 21, 2017
- Initial Release
