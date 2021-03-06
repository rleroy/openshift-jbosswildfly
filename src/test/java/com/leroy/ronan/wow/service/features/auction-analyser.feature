@analyse
Feature: Auction analyser

Scenario: Selling not craftable and not found item
Given there is nothing to sell
Given item is not craftable
 When I want to sell this item
 Then price should be 0
 
Scenario: Selling not craftable found item
Given there is an offer for 5 of this item at 500
Given item is not craftable
 When I want to sell this item
 Then price should be 99
 
Scenario: buying 1 found item
Given there is an offer for 1 of this item at 100
Given item is not craftable
 When I want to buy this item
 Then price should be 100
 
Scenario: buying 10 found item
Given there is an offer for 5 of this item at 500
Given there is an offer for 5 of this item at 1000
Given item is not craftable
 When I want to buy 10 of this item
 Then price should be 1500
 
Scenario: buying 20 found item
Given there is an offer for 5 of this item at 500
Given there is an offer for 5 of this item at 1000
Given item is not craftable
 When I want to buy 20 of this item
 Then it should be impossible
 
Scenario: Analysing non craftable and not found item
Given there is nothing to sell
Given item is not craftable
 When I want to analyse the price of this item
 Then analysis should return no buy price
 Then analysis should return no crafting price
 Then analysis should return no crafting recipe
  
Scenario: Analysing craftable and not found item
Given there is nothing to sell
Given item is craftable
 When I want to analyse the price of this item
 Then analysis should return no buy price
 Then analysis should return no crafting price
 Then analysis should return a crafting recipe
  
Scenario: Analysing craftable and found
Given there is an offer for 1 of this item at 100
Given item is craftable
 When I want to analyse the price of this item
 Then analysis should return 100 as buy price
 Then analysis should return no crafting price
 Then analysis should return a crafting recipe

Scenario: Building craftable price
Given there is an offer for 1 of this item at 100
Given item is craftable
Given recipe for item means take 5 units of reagent to get 1 item
Given there is an offer for 5 of this reagent at 50
 When I want to analyse the price of this item
 Then analysis should return 100 as buy price
 Then analysis should return 50 as crafting price
 Then analysis should return a crafting recipe
 