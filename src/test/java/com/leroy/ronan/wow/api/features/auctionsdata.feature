@wow
Feature: Gathering auction data

@wip
Scenario: Auction data
Given region is "eu"
Given realm name is "Sargeras"
 When I get the auctions
 Then I should get the list of auctions
  And I can analyse the auctions of "Pamynette" 
  And I can analyse prices