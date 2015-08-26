Feature: Generate an avatar from a character

Scenario: Make a multi characters avatar
Given region is "eu"
Given realm name is "Sargeras"
Given I want character name "Berenos" in image
Given I want character name "Rodd" in image
 When I get the avatar
 Then an avatar is available

Scenario: Make a 1 characters avatar with accents
Given region is "eu"
Given realm name is "Sargeras"
Given I want character name "Jül" in image
 When I get the avatar
 Then an avatar is available
 