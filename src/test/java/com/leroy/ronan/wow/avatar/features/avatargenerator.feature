Feature: Generate an avatar from a character

Scenario: Make a 2 characters avatar
Given region is "eu"
Given realm name is "Sargeras"
Given I want character name "Pamynx" in image
Given I want character name "Aphykith" in image
Given I want character name "Pamynette" in image
Given I want character name "Pamyniste" in image
Given I want character name "Pamalynx" in image
Given I want character name "Pamladynx" in image
Given I want character name "Gromalynx" in image
 When I get the avatar
 Then an avatar is available

Scenario: Make a 1 characters avatar
Given region is "eu"
Given realm name is "Sargeras"
Given I want character name "Pamynx" in image
 When I get the avatar
 Then an avatar is available
 