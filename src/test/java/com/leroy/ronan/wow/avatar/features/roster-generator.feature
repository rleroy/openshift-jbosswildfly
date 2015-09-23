@avatar
Feature: Generate an avatar from a guild

Scenario: Make an image from the guild roster
Given region is "eu"
Given realm name is "Sargeras"
Given guild name is "Les Sapins de la Horde"
 When I get the roster image
 Then the roster image is available
 