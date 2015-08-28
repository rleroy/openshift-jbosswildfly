Feature: Gathering guild data

Background:
Given region is "eu"
Given realm name is "Sargeras"

Scenario: Character data
Given character name is "Pamynx"
 When I get the character data
 Then I am able to know the ilvl of this character 
 Then I am able to know the achievementPoints of this character 

@ignore
Scenario: Character data
Given character name is "Aphykith"
 When I get the character data
 Then I am able to know this character is a reroll of "Pamynx" in realm "Sargeras"
