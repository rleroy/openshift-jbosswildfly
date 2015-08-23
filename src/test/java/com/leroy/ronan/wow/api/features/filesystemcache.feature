Feature: Handle cache of the BattleNet data
The data gathered from the BattleNetAPI is cached in memory and in the file system.

Background:
Given region is "eu"
Given realm name is "Sargeras"
Given character name is "Pamynx"

Scenario: Memory cache
Given character data does not exist in the file system
 When I get the character data
 When I get the character data
 Then the API was only called once
 Then data for the character has been saved in the file system

Scenario: File cache
Given character data exists in the file system
 When I get the character data
 Then the API was never called
