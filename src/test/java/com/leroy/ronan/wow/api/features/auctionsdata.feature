@wow
Feature: Auction analyser

Scenario: Auction data
Given region is "eu"
Given realm name is "Sargeras"
 When I get the auctions
 Then I should get the list of auctions
  And I can analyse the auctions of "Pamynette" 

Scenario: Crafting ROI
Given region is "eu"
Given realm name is "Sargeras"
Given item is 127760 / "Immaculate Critical Strike Taladite"
 When I get the auctions
  And I get the reagents of this item
 Then I can analyse its cost
 