Feature: Cache service

Scenario: Read once
Given a synchronized cache service
Given data is ok
Given file system data is fresh
Given memory data is empty
 When I access 10 time the data at once
 Then data should have been read once

Scenario: Load once
Given a synchronized cache service
Given data is ok
Given file system data is empty
Given memory data is empty
 When I access 10 time the data at once
 Then data should have been loaded once
 Then data should have been written once
 