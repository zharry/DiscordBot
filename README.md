# DiscordBot
Paragon Discord Bot, made by Harry 

### Functions
- Elo Checking with Agora.gg API
- Custom Ranking system based on KDA, W/L and ELO
- Live game checking

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

#### Aug 17, 2017
- Added Stat function
##### Stat Calculation
	 * For each 1% win rate (above 50%), add 1pts 
	 * For each 0.15 KDA (above 2), add 1pt
	 * 
	 * Elo Ranges: 
	 * Below 1300: Silver/Bronze (-5pts)
	 * 1300-1400: Low Gold (-1pt) 
	 * 1400-1500: Gold (+0pts) 
	 * 1500-1600: Plat (+2pts) 
	 * 1600-1700: High Plat (+5pts) 
	 * 1700-2000: Diamond (+10pts) 
	 * Above 2000: Masters (+20pts)
	 * 
	 * Point Scores: 
	 * Below 0: Boosted 
	 * 0-5: Trade 
	 * 5-10: Average 
	 * 10-20: Good 
	 * Above 30: Excellent

#### Aug 16, 2017
- Renamed bot from DiscordBot to ParaBot
- Updated Elo function to match Agora.gg API v1 and Paragon's v.42 update
- Changed JSON parser from *org.json* to *javax.json*

#### Prior to May 21, 2017
- Initial Release
