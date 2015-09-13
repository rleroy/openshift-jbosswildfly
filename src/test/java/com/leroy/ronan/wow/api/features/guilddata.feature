@wow
Feature: Gathering guild data

Scenario: Guild data
Given region is "eu"
Given realm name is "Sargeras"
Given guild name is "Les Sapins de la Horde"
 When I get the member list
 Then a character with name "Pamynx" is in the list
